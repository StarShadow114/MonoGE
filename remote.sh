#!/bin/bash
# ============================================================
# remote.sh — Monolith Engine 远程仓库管理 (gh 封装)
# 用法: ./remote.sh <command> [options]
# ============================================================

set -e

REPO="StarShadow114/MonoGE"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

info()    { echo -e "${CYAN}[remote]${NC} $1"; }
success() { echo -e "${GREEN}[remote]${NC} $1"; }
warn()    { echo -e "${YELLOW}[remote]${NC} $1"; }
die()     { echo -e "${RED}[remote] FATAL:${NC} $1"; exit 1; }
header()  { echo; echo -e "${BOLD}═══ $1 ═══${NC}"; echo; }

check_gh() {
    command -v gh &>/dev/null || die "gh 未安装 (apt install gh)"
    gh auth status &>/dev/null || die "gh 未登录，先跑 gh auth login"
}

check_repo() {
    local d="${MONOLITE_DIR:-$HOME/monolite}"
    cd "$d" 2>/dev/null || die "$d 不存在"
    [ -d ".git" ] || die "不是 git 仓库"
}

latest_run_id() {
    local id
    id=$(gh run list -R "$REPO" --limit 1 --json databaseId --jq '.[0].databaseId' 2>/dev/null)
    if [ -z "$id" ] || [ "$id" = "null" ]; then
        echo ""
        return 1
    fi
    echo "$id"
}

latest_failed_run_id() {
    local id
    id=$(gh run list -R "$REPO" --status failure --limit 1 --json databaseId --jq '.[0].databaseId' 2>/dev/null)
    if [ -z "$id" ] || [ "$id" = "null" ]; then
        echo ""
        return 1
    fi
    echo "$id"
}

CMD="${1:-}"
shift || true

case "$CMD" in

# ---------- ci / status ----------
ci|status)
    check_gh
    header "CI 状态 ($REPO)"

    W=$(tput cols 2>/dev/null || echo 80)
    [ "$W" -lt 40 ] && W=40
    W_STATUS=12
    W_BRANCH=10
    W_ID=12
    W_TITLE=$(( W - W_STATUS - W_BRANCH - W_ID - 6 ))
    [ "$W_TITLE" -lt 10 ] && W_TITLE=10

    printf "${BOLD}%-12s %-10s %-${W_TITLE}s %s${NC}\n" "STATUS" "BRANCH" "TITLE" "RUN_ID"
    printf "%s\n" "$(printf '─%.0s' $(seq 1 "$W"))"

    gh run list -R "$REPO" --limit 8 \
        --json status,conclusion,headBranch,displayTitle,databaseId \
        --jq '.[] | "\(.status)|\(.conclusion // "-")|\(.headBranch)|\(.displayTitle)|\(.databaseId)"' \
    | while IFS='|' read -r st co br ti id; do
        case "$st" in
            completed)
                case "$co" in
                    success)    st_fmt="${GREEN}✓${NC} success" ;;
                    failure)    st_fmt="${RED}✗${NC} failure" ;;
                    cancelled)  st_fmt="${YELLOW}■${NC} cancelled" ;;
                    *)          st_fmt="? $co" ;;
                esac ;;
            in_progress) st_fmt="${YELLOW}…${NC} running" ;;
            queued)      st_fmt="${CYAN}⏳${NC} queued" ;;
            *)           st_fmt="$st" ;;
        esac

        if [ ${#ti} -gt $((W_TITLE - 1)) ]; then
            ti_trunc="${ti:0:$((W_TITLE - 4))}..."
        else
            ti_trunc="$ti"
        fi

        printf "%-20s %-10s %-${W_TITLE}s %s\n" "$(echo -e "$st_fmt")" "$br" "$ti_trunc" "$id"
    done
    ;;

# ---------- fails ----------
fails)
    check_gh
    header "最近失败 runs"
    gh run list -R "$REPO" --status failure --limit 5 \
        --json databaseId,headBranch,displayTitle,createdAt \
        --jq '.[] | "  ID:\(.databaseId)  \(.headBranch)  \(.displayTitle[0:35])  \(.createdAt[0:16])"'
    echo ""
    info "查看失败日志: ./remote.sh fail"
    ;;

# ---------- log ----------
log)
    check_gh
    RUN_ID="${1:-}"
    if [ -z "$RUN_ID" ]; then
        RUN_ID=$(latest_run_id) || die "没有找到任何 workflow run"
        shift 2>/dev/null || true
    fi
    header "Run #$RUN_ID 完整日志"
    gh run view "$RUN_ID" -R "$REPO" --log
    ;;

# ---------- fail ----------
fail|error)
    check_gh
    RUN_ID="${1:-}"
    if [ -z "$RUN_ID" ]; then
        RUN_ID=$(latest_failed_run_id) || die "没有找到失败的 workflow run"
    fi
    header "Run #$RUN_ID 失败日志"
    gh run view "$RUN_ID" -R "$REPO" --log-failed
    ;;

# ---------- rerun ----------
rerun)
    check_gh
    TARGET="${1:-}"
    if [ -z "$TARGET" ]; then
        TARGET=$(latest_run_id) || die "没有找到 run"
    fi
    header "重新触发 Run #$TARGET"
    gh run rerun "$TARGET" -R "$REPO" --force || die "rerun 失败"
    success "已触发重新构建"
    ;;

# ---------- trigger ----------
trigger)
    check_gh
    BRANCH="${1:-dev}"
    WORKFLOW="${2:-build-mgebt.yml}"
    header "触发 workflow $WORKFLOW (分支: $BRANCH)"
    gh workflow run "$WORKFLOW" -R "$REPO" --ref "$BRANCH" || die "触发失败"
    success "已触发 $WORKFLOW on $BRANCH"
    ;;

# ---------- watch ----------
watch)
    check_gh
    header "监听 CI (Ctrl+C 退出)"
    gh run watch -R "$REPO"
    ;;

# ---------- branches ----------
branches|branch)
    check_gh
    header "远程分支"
    gh api "repos/$REPO/branches?per_page=100" --jq '.[] | "\(.name)\t\(.commit.sha[0:7])\t\(.protected)"' | column -t -s $'\t'
    echo ""
    info "本地分支:"
    check_repo && git branch -a 2>/dev/null || true
    ;;

# ---------- delete-branch ----------
delete-branch)
    check_gh
    BRANCH="${1:-}"
    [ -z "$BRANCH" ] && die "用法: ./remote.sh delete-branch <branch-name>"
    header "删除远程分支: $BRANCH"
    gh api -X DELETE "repos/$REPO/git/refs/heads/$BRANCH" && success "已删除 $BRANCH" || die "删除失败 (分支不存在或无权限)"
    ;;

# ---------- compare ----------
compare)
    check_gh
    check_repo
    header "本地 vs 远程对比"
    git fetch origin 2>/dev/null || warn "fetch 失败(网络问题)，显示缓存状态"

    for branch in main dev; do
        LOCAL=$(git rev-parse "$branch" 2>/dev/null || echo "N/A")
        REMOTE=$(git rev-parse "origin/$branch" 2>/dev/null || echo "N/A")
        echo -n "  $branch: "
        if [ "$LOCAL" = "$REMOTE" ]; then
            echo -e "${GREEN}同步${NC} ($LOCAL)"
        elif [ "$LOCAL" = "N/A" ]; then
            echo -e "${YELLOW}本地无此分支${NC}"
        elif [ "$REMOTE" = "N/A" ]; then
            echo -e "${YELLOW}远程无此分支${NC}"
        else
            echo -e "${RED}不同步${NC}"
            echo "    本地:  $LOCAL"
            echo "    远程:  $REMOTE"
        fi
    done
    ;;

# ---------- pr ----------
pr)
    check_gh
    SUB="${1:-list}"
    shift || true
    case "$SUB" in
        list)    gh pr list -R "$REPO" ;;
        create)  gh pr create -R "$REPO" "$@" ;;
        view)    gh pr view -R "$REPO" ;;
        close)   gh pr close -R "$REPO" "$@" ;;
        *)       gh pr "$SUB" -R "$REPO" "$@" ;;
    esac
    ;;

# ---------- release ----------
release)
    check_gh
    SUB="${1:-list}"
    shift || true
    case "$SUB" in
        list)    gh release list -R "$REPO" ;;
        view)    gh release view -R "$REPO" "$@" ;;
        create)  gh release create -R "$REPO" "$@" ;;
        download) gh release download -R "$REPO" "$@" ;;
        *)       gh release "$SUB" -R "$REPO" "$@" ;;
    esac
    ;;

# ---------- artifact ----------
artifact|artifacts)
    check_gh
    SUB="${1:-list}"
    shift || true
    case "$SUB" in
        list)
            RUN_ID="${1:-}"
            if [ -z "$RUN_ID" ]; then
                RUN_ID=$(latest_run_id) || die "没有 run"
            fi
            header "Run #$RUN_ID 产物"
            gh run view "$RUN_ID" -R "$REPO" --json artifacts \
                --jq '.artifacts[]? | "\(.name)  \(.sizeInBytes) bytes  ID:\(.databaseId)"' 2>/dev/null \
                || warn "该 run 没有产物或 gh 版本不支持"
            ;;
        download)
            RUN_ID=""
            DEST=""
            ARTIFACT_NAME=""
            while [ $# -gt 0 ]; do
                case "$1" in
                    -r|--run)   RUN_ID="$2"; shift 2 ;;
                    -d|--dest)  DEST="$2"; shift 2 ;;
                    -n|--name)  ARTIFACT_NAME="$2"; shift 2 ;;
                    *)          DEST="${1}"; shift ;;
                esac
            done
            if [ -z "$RUN_ID" ]; then
                RUN_ID=$(latest_run_id) || die "没有 run"
            fi
            if [ -z "$DEST" ]; then
                DEST="./artifacts"
            fi
            mkdir -p "$DEST"
            header "下载 Run #$RUN_ID 产物到 $DEST"
            if [ -n "$ARTIFACT_NAME" ]; then
                gh run download "$RUN_ID" -R "$REPO" -n "$ARTIFACT_NAME" -D "$DEST" || die "下载失败"
            else
                gh run download "$RUN_ID" -R "$REPO" -D "$DEST" || die "下载失败"
            fi
            success "产物已下载到 $DEST"
            ls -lh "$DEST/"
            ;;
        *)
            die "用法: ./remote.sh artifact [list [run_id] | download [-r run_id] [-n name] [-d dest]]"
            ;;
    esac
    ;;

# ---------- net ----------
net|network)
    header "网络诊断"
    info "GitHub.com 连通性:"
    if curl -sI --connect-timeout 5 https://github.com >/dev/null 2>&1; then
        success "GitHub HTTP OK"
    else
        warn "GitHub HTTP 不通"
    fi

    info "API 连通性:"
    gh api "repos/$REPO" --jq '"  Repo: \(.full_name)\n  Default branch: \(.default_branch)\n  Updated: \(.updated_at)"' 2>/dev/null || warn "API 不通"

    info "DNS 解析:"
    getent hosts github.com 2>/dev/null || nslookup github.com 2>/dev/null | head -5 || warn "DNS 解析失败"

    info "代理设置:"
    env | grep -i proxy || echo "  无代理环境变量"
    local_proxy=$(git config --global http.proxy 2>/dev/null)
    if [ -n "$local_proxy" ]; then
        echo "  git proxy: $local_proxy"
    else
        echo "  git proxy 未设置"
    fi
    ;;

# ---------- repo ----------
repo|info)
    check_gh
    header "仓库信息"
    gh repo view "$REPO" --json name,description,defaultBranchRef,isEmpty,createdAt,updatedAt,pushedAt,sshUrl,url --jq '
"  名称: \(.name)
  描述: \(.description // "无")
  默认分支: \(.defaultBranchRef.name)
  空仓库: \(.isEmpty)
  创建: \(.createdAt)
  更新: \(.updatedAt)
  最后推送: \(.pushedAt)
  URL: \(.url)
  SSH: \(.sshUrl)"
    '
    ;;

# ---------- clean ----------
clean)
    check_gh
    header "远程分支清理 (保留 main/dev)"
    info "以下远程分支可被清理:"
    gh api "repos/$REPO/branches?per_page=100" --jq '.[].name' | while read -r b; do
        case "$b" in
            main|dev|develop|master) ;;
            *) echo "  可删: $b" ;;
        esac
    done
    echo ""
    warn "确认删除? 用法: ./remote.sh delete-branch <name>"
    ;;

# ---------- url ----------
url)
    echo "HTTPS: https://github.com/$REPO.git"
    echo "SSH:   git@github.com:$REPO.git"
    ;;

# ---------- help ----------
help|-h|--help|"")
    echo ""
    echo "remote.sh — Monolith Engine 远程仓库管理"
    echo "========================================"
    echo ""
    echo "CI 相关:"
    echo "  ci, status              CI 状态表"
    echo "  fails                    只看失败 runs"
    echo "  log [run_id]             完整日志"
    echo "  fail, error [run_id]     失败日志"
    echo "  rerun [run_id]           重新触发"
    echo "  trigger [branch] [wf]    手动触发 (默认 dev + build-mgebt.yml)"
    echo "  watch                    实时监听"
    echo ""
    echo "分支 & 仓库:"
    echo "  branches                 远程分支"
    echo "  delete-branch <name>     删远程分支"
    echo "  compare                  本地 vs 远程"
    echo "  clean                    列出可清理分支"
    echo "  repo                     仓库信息"
    echo ""
    echo "产物:"
    echo "  artifact list [run_id]                    列出产物"
    echo "  artifact download [-r id] [-n name] [-d dest]"
    echo ""
    echo "工具:"
    echo "  net                      网络诊断"
    echo "  url                      克隆地址"
    echo "  pr [list|create|view]    PR 管理"
    echo "  release [list|download]   Release 管理"
    echo ""
    ;;

*)
    die "未知命令: $CMD (用 ./remote.sh help 查看帮助)"
    ;;

esac

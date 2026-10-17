#!/bin/bash
# ============================================================
# sync.sh — Monolith Engine 增量同步 + Git 操作
# 用法: ./sync.sh <command> [options]
# ============================================================

set -e

SRC="/sdcard/project/MonoGE/MonoGE"
DST="$HOME/monolite"
REPO="$DST"
SELF="sync.sh"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

info()    { echo -e "${CYAN}[sync]${NC} $1"; }
success() { echo -e "${GREEN}[sync]${NC} $1"; }
warn()    { echo -e "${YELLOW}[sync]${NC} $1"; }
die()     { echo -e "${RED}[sync] FATAL:${NC} $1"; exit 1; }

check_dirs() {
    [ -d "$SRC" ] || die "源目录不存在: $SRC"
    [ -d "$DST" ] || die "目标目录不存在: $DST"
}

check_git() {
    cd "$REPO" || die "无法进入 $REPO"
    [ -d ".git" ] || die "$REPO 不是 git 仓库"
}

do_rsync() {
    local dry="$1"
    check_dirs

    local exclude_list=(
        --exclude=".choccy/"
        --exclude="session.json"
        --exclude="build*/"
        --exclude=".git/"
        --exclude="*.so"
        --exclude="cmake-build-*/"
        --exclude="${SELF}" --exclude="remote.sh"
        --exclude="*.sh"
    )

    if [ "$dry" = "1" ]; then
        info "[DRY] 增量同步预览:"
        rsync -avn "${exclude_list[@]}" "$SRC/" "$DST/"
        return 0
    fi

    info "增量同步 $SRC/ → $DST/ (仅变更文件)"
    rsync -av --delete "${exclude_list[@]}" "$SRC/" "$DST/"
    success "同步完成"
}

# ---------- 参数解析 ----------
CMD="${1:-}"
shift || true

COPY=0
COMMIT_MSG=""
PUSH=0
MERGE=0
MERGE_TARGET="main"
DRY=0

while [ $# -gt 0 ]; do
    case "$1" in
        --copy|-c)      COPY=1 ;;
        --msg|-m)       COMMIT_MSG="$2"; shift ;;
        --push|-p)      PUSH=1 ;;
        --merge|-mg)    MERGE=1; MERGE_TARGET="${2:-main}"; shift ;;
        --dry|-d)       DRY=1 ;;
        *)              warn "未知参数: $1" ;;
    esac
    shift || true
done

# ============================================================
# 命令分发
# ============================================================

case "$CMD" in

copy)
    do_rsync "$DRY"
    ;;

push-back|pb)
    check_dirs

    EXCLUDE_LIST=(
        --exclude=".choccy/"
        --exclude="session.json"
        --exclude="build*/"
        --exclude=".git/"
        --exclude="*.so"
        --exclude="cmake-build-*/"
        --exclude="${SELF}"
        --exclude="remote.sh"
        --exclude="*.sh"
        --exclude="src.bak/"
    )

    if [ "$DRY" -eq 1 ]; then
        info "[DRY] 反向同步预览: $DST/mgebt/ → $SRC/mgebt/"
        rsync -avn "${EXCLUDE_LIST[@]}" "$DST/mgebt/" "$SRC/mgebt/"
        exit 0
    fi

    info "反向同步 $DST/mgebt/ → $SRC/mgebt/ (仅变更文件)"
    rsync -av "${EXCLUDE_LIST[@]}" "$DST/mgebt/" "$SRC/mgebt/"
    success "反向同步完成"
    ;;

commit)
    check_git
    cd "$REPO"

    if [ "$COPY" -eq 1 ]; then
        do_rsync "$DRY"
    fi

    git add .
    if [ -z "$COMMIT_MSG" ]; then
        COMMIT_MSG="sync: $(date '+%Y-%m-%d %H:%M')"
    fi

    git commit -m "$COMMIT_MSG" || warn "没有新改动可交"

    if [ "$PUSH" -eq 1 ]; then
        BRANCH=$(git branch --show-current)
        git push origin "$BRANCH" || die "推送失败"
        success "已推送 $BRANCH"
    fi
    ;;

promote)
    check_git
    cd "$REPO"

    [ "$DRY" -eq 1 ] && { info "[DRY] 将执行: checkout main && merge dev && push main"; exit 0; }

    git checkout dev || die "无法切换到 dev"
    if [ -n "$(git status --porcelain)" ]; then
        die "dev 分支有未提交的改动，先 commit 或 stash"
    fi

    info "切换到 main..."
    git checkout main || die "无法切换到 main"
    git pull origin main || warn "pull main 失败（可能网络问题）"

    info "合并 dev → main..."
    if git merge dev --no-edit; then
        success "dev → main 合并成功"
    else
        die "合并冲突！解决步骤:\n  1. git status 看冲突文件\n  2. 编辑解决\n  3. git add <file>\n  4. git commit\n  5. 重跑 ./sync.sh promote"
    fi

    if [ "$PUSH" -eq 1 ]; then
        git push origin main || die "推送 main 失败"
        success "main 已推送到 origin"
    fi

    git checkout dev
    success "已切回 dev"
    ;;

merge)
    check_git
    cd "$REPO"

    TARGET="${MERGE_TARGET:-main}"
    [ "$DRY" -eq 1 ] && { info "[DRY] git merge dev → $TARGET"; exit 0; }

    git checkout "$TARGET" || die "无法切换到 $TARGET"
    git pull origin "$TARGET" 2>/dev/null || true

    if git merge dev --no-edit; then
        success "合并成功"
    else
        die "合并冲突！手动解决"
    fi

    [ "$PUSH" -eq 1 ] && { git push origin "$TARGET" || die "推送失败"; }
    ;;

push)
    check_git
    cd "$REPO"
    BRANCH=$(git branch --show-current)
    git push origin "$BRANCH" || die "推送失败"
    success "推送完成"
    ;;

pull)
    check_git
    cd "$REPO"
    BRANCH=$(git branch --show-current)
    git pull origin "$BRANCH" || die "拉取失败"
    success "拉取完成"
    ;;

status)
    check_git
    cd "$REPO"
    info "分支: $(git branch --show-current)"
    git status --short
    ;;

ci)
    info "CI 状态:"
    gh run list -R StarShadow114/MonoGE --limit 5 || die "gh 失败"
    ;;

all)
    check_dirs
    check_git
    cd "$REPO"

    git show-ref --verify --quiet refs/heads/dev 2>/dev/null || git checkout -b dev
    git checkout dev || die "无法切换到 dev"

    info "═══ Step 1/3: 增量同步 ═══"
    do_rsync "$DRY"
    success "Step 1 完成"

    info "═══ Step 2/3: 提交 ═══"
    git add .
    if [ -z "$COMMIT_MSG" ]; then
        COMMIT_MSG="feat: $(date '+%Y-%m-%d %H:%M')"
    fi
    if git commit -m "$COMMIT_MSG"; then
        success "Step 2 完成: $COMMIT_MSG"
    else
        warn "Step 2: 无新改动，跳过"
    fi

    info "═══ Step 3/3: 推送 dev ═══"
    git push origin dev || die "Step 3 FAILED: dev 推失败"

    echo ""
    echo "========================================"
    success "all 完成！dev 已更新并推送"
    echo "  如需合并到 main，单独运行:"
    echo "  ./sync.sh promote --push"
    echo "========================================"
    ;;

help|-h|--help|"")
    echo ""
    echo "Monolith Engine Sync Script"
    echo "==========================="
    echo ""
    echo "Commands:"
    echo "  copy              增量复制 FUSE → monolite"
    echo "  push-back, pb     反向同步 monolite/mgebt → FUSE/mgebt"
    echo "  commit [opts]     提交当前变更"
    echo "  promote --push    合并 dev → main 并推送"
    echo "  merge             合并 dev → 指定分支"
    echo "  push              推送当前分支"
    echo "  pull              拉取当前分支"
    echo "  status            显示状态"
    echo "  ci                查看 CI"
    echo "  all               全流程: copy + commit + push dev"
    echo ""
    echo "Options:"
    echo "  --copy, -c        commit 前先同步"
    echo "  --msg, -m <msg>   提交信息"
    echo "  --push, -p        操作后推送"
    echo "  --dry, -d         预览模式"
    echo ""
    echo "典型工作流:"
    echo "  ./sync.sh copy --dry        # 预览"
    echo "  ./sync.sh copy              # 同步"
    echo "  ./sync.sh push-back --dry   # 反向预览"
    echo "  ./sync.sh push-back         # 反向同步回 FUSE"
    echo "  ./sync.sh all --msg 'fix'   # 提交+推 dev"
    echo ""
    ;;

*)
    die "未知命令: $CMD (用 ./sync.sh help 查看帮助)"
    ;;

esac

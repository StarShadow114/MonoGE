#!/bin/bash
# ============================================================
# sync.sh — Monolith Engine 增量同步 + Git 操作
# 用法: ./sync.sh <command> [options]
# 特性: 增量 rsync | 出错即停 | 自排除
# ============================================================

set -e  # 任何命令非零退出立即终止

SRC="/sdcard/project/MonoGE/MonoGE"
DST="$HOME/monolite"
REPO="$DST"
SELF="sync.sh"

# ---------- 颜色 ----------
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

info()    { echo -e "${CYAN}[sync]${NC} $1"; }
success() { echo -e "${GREEN}[sync]${NC} $1"; }
warn()    { echo -e "${YELLOW}[sync]${NC} $1"; }
die()     { echo -e "${RED}[sync] FATAL:${NC} $1"; exit 1; }

# ---------- 前置检查 ----------
check_dirs() {
    [ -d "$SRC" ] || die "源目录不存在: $SRC"
    [ -d "$DST" ] || die "目标目录不存在: $DST"
}

check_git() {
    cd "$REPO" || die "无法进入 $REPO"
    [ -d ".git" ] || die "$REPO 不是 git 仓库"
}

# ---------- rsync 增量同步（核心） ----------
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
        --exclude="${SELF}"
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
STATUS=0
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

# ---------- copy ----------
copy)
    do_rsync "$DRY"
    ;;

# ---------- commit ----------
commit)
    check_git
    cd "$REPO"

    if [ "$COPY" -eq 1 ]; then
        do_rsync "$DRY"
    fi

    info "添加变更..."
    git add .

    if [ -z "$COMMIT_MSG" ]; then
        COMMIT_MSG="sync: $(date '+%Y-%m-%d %H:%M')"
    fi

    info "提交: $COMMIT_MSG"
    git commit -m "$COMMIT_MSG" || warn "没有新改动可提交"

    if [ "$PUSH" -eq 1 ]; then
        BRANCH=$(git branch --show-current)
        info "推送 $BRANCH..."
        git push origin "$BRANCH" || die "推送失败"
        success "推送完成"
    fi
    ;;

# ---------- merge ----------
merge)
    check_git
    cd "$REPO"

    TARGET="${MERGE_TARGET:-main}"
    info "合并 dev → $TARGET"

    [ "$DRY" -eq 1 ] && { info "[DRY] git checkout $TARGET && git merge dev"; exit 0; }

    git checkout dev || die "无法切换到 dev"
    git checkout "$TARGET" || die "无法切换到 $TARGET"
    git pull origin "$TARGET" 2>/dev/null || true

    if git merge dev --no-edit; then
        success "dev → $TARGET 合并成功"
    else
        die "合并冲突！手动解决: git status → 编辑 → git add → git commit"
    fi

    if [ "$PUSH" -eq 1 ]; then
        git push origin "$TARGET" || die "推送 $TARGET 失败"
        success "已推送 $TARGET"
    fi

    git checkout dev
    success "切回 dev"
    ;;

# ---------- push ----------
push)
    check_git
    cd "$REPO"
    BRANCH=$(git branch --show-current)
    info "推送 $BRANCH..."
    git push origin "$BRANCH" || die "推送失败"
    success "推送完成"
    ;;

# ---------- pull ----------
pull)
    check_git
    cd "$REPO"
    BRANCH=$(git branch --show-current)
    info "拉取 $BRANCH..."
    git pull origin "$BRANCH" || die "拉取失败"
    success "拉取完成"
    ;;

# ---------- status ----------
status)
    check_git
    cd "$REPO"
    info "分支: $(git branch --show-current)"
    git status --short
    ;;

# ---------- ci ----------
ci)
    info "CI 状态:"
    gh run list -R StarShadow114/MonoGE --limit 5 || die "gh 命令失败(网络?)"
    ;;

# ---------- all: 全流程，出错即停 ----------
all)
    check_dirs
    check_git
    cd "$REPO"

    # 确保 dev 分支存在
    git show-ref --verify --quiet refs/heads/dev || git checkout -b dev

    # ---- Step 1: 切 dev + 增量同步 ----
    info "═══ Step 1/5: 切到 dev + 增量同步 ═══"
    git checkout dev || die "Step 1 FAILED: 无法切换到 dev"
    do_rsync "$DRY"
    success "Step 1 完成"

    # ---- Step 2: 提交 ----
    info "═══ Step 2/5: 提交变更 ═══"
    git add .
    if [ -z "$COMMIT_MSG" ]; then
        COMMIT_MSG="feat: $(date '+%Y-%m-%d %H:%M')"
    fi
    if git commit -m "$COMMIT_MSG"; then
        success "Step 2 完成: $COMMIT_MSG"
    else
        warn "Step 2: 无新改动，跳过提交"
    fi

    # ---- Step 3: 推送 dev ----
    info "═══ Step 3/5: 推送 dev ═══"
    if git push origin dev; then
        success "Step 3 完成"
    else
        die "Step 3 FAILED: dev 推送失败 (可能需要 git pull --rebase)"
    fi

    # ---- Step 4: 合并 dev → main ----
    info "═══ Step 4/5: 合并 dev → main ═══"
    git checkout main || die "Step 4 FAILED: 无法切换到 main"
    git pull origin main 2>/dev/null || true

    if git merge dev --no-edit; then
        success "Step 4 完成: dev → main 合并成功"
    else
        die "Step 4 FAILED: 合并冲突！解决后重跑"
    fi

    # ---- Step 5: 推送 main ----
    info "═══ Step 5/5: 推送 main ═══"
    if git push origin main; then
        success "Step 5 完成"
    else
        die "Step 5 FAILED: main 推送失败"
    fi

    # 切回 dev
    git checkout dev
    echo ""
    echo "========================================"
    success "全流程完成！dev → main 已同步"
    echo "========================================"
    ;;

# ---------- help ----------
help|-h|--help|"")
    echo ""
    echo "Monolith Engine Sync Script (增量 + 容错)"
    echo "=========================================="
    echo ""
    echo "Commands:"
    echo "  copy             增量复制 FUSE → monolite (切 dev)"
    echo "  commit          提交 [--copy 先同步] [--push 后推送]"
    echo "  merge [--merge main]  合并 dev → main"
    echo "  push            推送当前分支"
    echo "  pull            拉取当前分支"
    echo "  status          显示 git 状态"
    echo "  ci              查看 CI"
    echo "  all             全流程 (出错即停)"
    echo ""
    echo "Options:"
    echo "  --copy, -c      执行文件同步"
    echo "  --msg, -m <msg> 提交信息"
    echo "  --push, -p      操作后推送"
    echo "  --merge, -mg    合并目标分支 (默认 main)"
    echo "  --dry, -d       预览模式"
    echo ""
    echo "Examples:"
    echo "  ./sync.sh copy --dry"
    echo "  ./sync.sh copy"
    echo "  ./sync.sh commit --copy --msg 'fix: stdlib.h' --push"
    echo "  ./sync.sh merge --merge main --push"
    echo "  ./sync.sh all --msg 'feat: stage 2 EGL'"
    echo ""
    ;;

*)
    die "未知命令: $CMD (用 ./sync.sh help 查看帮助)"
    ;;

esac

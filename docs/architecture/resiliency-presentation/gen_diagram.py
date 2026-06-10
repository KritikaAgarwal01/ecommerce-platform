"""
Executive diagram for HVE Resiliency Research Skill.
Outputs a high-quality JPG suitable for presentation to leadership.
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import matplotlib.patheffects as pe

# ── Canvas ────────────────────────────────────────────────────────────────────
fig, ax = plt.subplots(figsize=(22, 15))
ax.set_xlim(0, 22)
ax.set_ylim(0, 15)
ax.axis('off')
fig.patch.set_facecolor('#0f172a')

# ── Colour palette ─────────────────────────────────────────────────────────────
BG        = '#0f172a'
INPUT_BG  = '#1e3a5f'
INPUT_BD  = '#3b82f6'
RES_BG    = '#064e3b'
RES_BD    = '#10b981'
GATE_BG   = '#713f12'
GATE_BD   = '#f59e0b'
PLAN_BG   = '#3b0764'
PLAN_BD   = '#a78bfa'
OUT_BG    = '#7f1d1d'
OUT_BD    = '#ef4444'
SKILL_BG  = '#1e3050'
SKILL_BD  = '#60a5fa'
TEXT_W    = '#f8fafc'
TEXT_S    = '#94a3b8'
ARROW     = '#64748b'
DASH_CLR  = '#f59e0b'

def box(ax, x, y, w, h, bg, bd, title, lines, title_size=9, line_size=7.5, radius=0.25):
    """Draw a rounded box with a bold title and sub-lines."""
    rect = FancyBboxPatch((x, y), w, h,
                          boxstyle=f"round,pad=0,rounding_size={radius}",
                          facecolor=bg, edgecolor=bd, linewidth=1.8, zorder=3)
    ax.add_patch(rect)
    cx = x + w / 2
    ty = y + h - 0.28
    ax.text(cx, ty, title, ha='center', va='top', fontsize=title_size,
            fontweight='bold', color=TEXT_W, zorder=4)
    sep_y = ty - 0.30
    ax.plot([x + 0.12, x + w - 0.12], [sep_y, sep_y],
            color=bd, linewidth=0.7, alpha=0.6, zorder=4)
    line_y = sep_y - 0.22
    for ln in lines:
        ax.text(cx, line_y, ln, ha='center', va='top', fontsize=line_size,
                color='#cbd5e1', zorder=4)
        line_y -= 0.26

def arrow(ax, x1, y1, x2, y2, color=ARROW, lw=2.0, dash=False):
    style = 'dashed' if dash else 'solid'
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle='->', color=color,
                                lw=lw, linestyle=style,
                                connectionstyle='arc3,rad=0.0'),
                zorder=5)

def label_arrow(ax, x, y, txt, color=TEXT_S, size=6.5):
    ax.text(x, y, txt, ha='center', va='center', fontsize=size,
            color=color, style='italic', zorder=6,
            bbox=dict(facecolor=BG, edgecolor='none', pad=1.5))

# ── Title ──────────────────────────────────────────────────────────────────────
ax.text(11, 12.6, 'HVE Resiliency Research Skill  -  How It Works',
        ha='center', va='top', fontsize=16, fontweight='bold', color=TEXT_W)
ax.text(11, 12.25, 'Automated code-level resiliency analysis  |  Evidence-only research  |  Full traceability from code line to ADO work item',
        ha='center', va='top', fontsize=9, color=TEXT_S)

# ── Phase labels (top strip) ───────────────────────────────────────────────────
phases = [
    (0.3,  1.8,  'INPUT'),
    (2.25, 4.2,  'SKILL ORCHESTRATOR'),
    (6.55, 4.8,  'PHASE 1 + 2  ·  Task Researcher'),
    (11.7, 3.1,  'PHASE 3  ·  Consolidation'),
    (15.1, 3.8,  'PHASE 4  ·  Task Planner'),
    (19.1, 2.6,  'OUTPUT'),
]
phase_colors = [INPUT_BD, SKILL_BD, RES_BD, GATE_BD, PLAN_BD, OUT_BD]
for (px, pw, ptxt), pc in zip(phases, phase_colors):
    ax.add_patch(FancyBboxPatch((px, 11.45), pw, 0.38,
                 boxstyle='round,pad=0,rounding_size=0.1',
                 facecolor=pc+'22', edgecolor=pc, linewidth=1.0, zorder=2))
    ax.text(px + pw/2, 11.64, ptxt, ha='center', va='center',
            fontsize=7.5, fontweight='bold', color=pc, zorder=3)

# ── 1  REPOSITORY (input) ─────────────────────────────────────────────────────
box(ax, 0.3, 8.5, 1.8, 2.7, INPUT_BG, INPUT_BD,
    'Repository',
    ['Application Code', 'Bicep / IaC', 'Helm Charts', 'Config & Env', 'Dockerfiles', 'Pipelines'],
    title_size=8.5)

# ── 2  SKILL.md orchestrator ───────────────────────────────────────────────────
box(ax, 2.25, 9.3, 4.2, 1.9, SKILL_BG, SKILL_BD,
    'SKILL.md  —  Orchestrator',
    ['Enforces phase order',
     'Auto-loads Platform Context',
     'Service Exclusion Rule',
     'Priority definitions P0 -> P3'],
    title_size=8.5)

# context sub-box inside orchestrator region
box(ax, 2.25, 8.3, 2.0, 0.85, '#162033', SKILL_BD,
    'Platform Context',
    ['Target regions · WAF rules'],
    title_size=7.5, line_size=7)

box(ax, 4.35, 8.3, 2.1, 0.85, '#162033', PLAN_BD,
    'Planner Context',
    ['Priority rules · Output templates'],
    title_size=7.5, line_size=7)

# ── 3  PHASE 1 — Core Research ─────────────────────────────────────────────────
box(ax, 6.55, 9.3, 2.3, 1.9, RES_BG, RES_BD,
    'Core Research',
    ['Prompts 0 - 7  (sequential)',
     'Architecture walkthrough',
     'Dependency inventory',
     'Zone / region assumptions',
     'Data & state risk',
     'Failure modes',
     'Observability gaps'],
    title_size=8.5, line_size=6.8)

# ── 4  Service Exclusion Gate ──────────────────────────────────────────────────
box(ax, 6.55, 8.05, 2.3, 1.05, GATE_BG, GATE_BD,
    'GATE  Service Exclusion',
    ['Prompt 1 confirms which services',
     'are actually used in the repo.',
     'All others permanently excluded.'],
    title_size=8, line_size=7)

# ── 5  PHASE 2 — Service Research ──────────────────────────────────────────────
box(ax, 6.55, 6.6, 2.3, 1.25, RES_BG, RES_BD,
    'Service Research',
    ['Prompts 8 - 19  (conditional)',
     'AKS · Cosmos DB · SQL · App GW',
     'Key Vault · Entra ID · Networking'],
    title_size=8.5, line_size=7)

# ── 6  Phase 3 — Consolidation ──────────────────────────────────────────────────
box(ax, 11.7, 9.3, 3.1, 1.9, RES_BG, RES_BD,
    'Consolidation',
    ['Merge all Phase 1 + 2 outputs',
     'Assign unique Finding IDs',
     'P0 -> P3 classification',
     'Every finding: file + line number',
     'Zero recommendations'],
    title_size=8.5, line_size=7)

box(ax, 11.7, 8.05, 3.1, 1.05, '#0a3328', RES_BD,
    'research-output.md',
    ['22 findings  ·  P0-001 through P2-003',
     'Full forensic evidence  ·  No design advice'],
    title_size=8, line_size=7)

# ── 7  Phase 4 — Planner Lock-In ───────────────────────────────────────────────
box(ax, 15.1, 9.9, 3.8, 1.3, PLAN_BG, PLAN_BD,
    'Evidence Lock-In  (Planner 0)',
    ['Research = fixed constraints',
     'Planner cannot add, remove, or soften findings',
     'Priority classifications are immutable'],
    title_size=8.5, line_size=7)

# ── 8  Three planning outputs ───────────────────────────────────────────────────
box(ax, 15.1, 8.4, 3.8, 1.3, PLAN_BG, PLAN_BD,
    'Master Report  (Planner 1)',
    ['Audience: Leadership + Architecture Board',
     'Remediation roadmap  ·  Open questions',
     'Full dependency map  ·  P0 -> P3 ordered'],
    title_size=8.5, line_size=7)

box(ax, 15.1, 6.9, 3.8, 1.3, PLAN_BG, PLAN_BD,
    'Developer Guide  (Planner 2)',
    ['Audience: Engineering Teams',
     'Per-finding: code examples in repo language',
     'Security hardening + failover patterns'],
    title_size=8.5, line_size=7)

box(ax, 15.1, 5.4, 3.8, 1.3, PLAN_BG, PLAN_BD,
    'Code-Level Assessment  (Planner 3)',
    ['Audience: Architecture Board / WAF Review',
     'WAF Reliability mapping  ·  IaC gap analysis',
     'Active/Active migration readiness score'],
    title_size=8.5, line_size=7)

# ── 9  ADO Backlog ──────────────────────────────────────────────────────────────
box(ax, 19.1, 6.85, 2.6, 2.55, OUT_BG, OUT_BD,
    'ADO Backlog',
    ['1 Epic',
     '5 Features',
     '22 User Stories',
     '─────────────────',
     'P0 -> P3 ordered',
     'Acceptance criteria',
     'Fully traceable to code'],
    title_size=8.5, line_size=7.5)

# ── Arrows: horizontal flow ─────────────────────────────────────────────────────
# repo → skill
arrow(ax, 2.1, 9.85, 2.25, 9.85, color=INPUT_BD, lw=2.2)
# skill → phase 1 core
arrow(ax, 6.45, 10.25, 6.55, 10.25, color=SKILL_BD, lw=2.2)
# phase 1 → gate (down)
arrow(ax, 7.7, 9.3, 7.7, 9.1, color=RES_BD, lw=2.0)
# gate → phase 2 (down)
arrow(ax, 7.7, 8.05, 7.7, 7.85, color=GATE_BD, lw=2.0)
# phase 2 → consolidation
arrow(ax, 8.85, 7.35, 11.7, 7.35, color=RES_BD, lw=2.2)
label_arrow(ax, 10.27, 7.5, 'individual\nresearch files', color=RES_BD, size=6.5)
# phase 1 → consolidation (at phase 1 level — direct)
arrow(ax, 8.85, 10.25, 11.7, 10.25, color=RES_BD, lw=1.6)
# consolidation doc → planner lock-in
arrow(ax, 14.8, 8.6, 15.1, 9.9+0.65, color=GATE_BD, lw=2.2)
label_arrow(ax, 14.95, 9.3, 'sole input\nto planner', color=GATE_BD, size=6.5)
# lock-in → master
arrow(ax, 16.65, 9.9, 16.65, 9.7, color=PLAN_BD, lw=2.0)
# lock-in → dev guide
arrow(ax, 17.5, 9.9, 17.5, 8.2, color=PLAN_BD, lw=1.8)
ax.annotate('', xy=(17.5, 8.2), xytext=(17.5, 9.9),
            arrowprops=dict(arrowstyle='->', color=PLAN_BD, lw=1.8), zorder=5)
# lock-in → code assessment
ax.annotate('', xy=(16.0, 6.7), xytext=(16.0, 9.9),
            arrowprops=dict(arrowstyle='->', color=PLAN_BD, lw=1.8), zorder=5)
# three docs → ADO
arrow(ax, 18.9, 9.05, 19.1, 8.9, color=OUT_BD, lw=2.0)
arrow(ax, 18.9, 7.55, 19.1, 8.0, color=OUT_BD, lw=2.0)
arrow(ax, 18.9, 6.05, 19.1, 7.3, color=OUT_BD, lw=2.0)

# ── Traceability callout ────────────────────────────────────────────────────────
tx, ty, tw, th = 0.3, 4.3, 21.4, 2.1
rect2 = FancyBboxPatch((tx, ty), tw, th,
                       boxstyle='round,pad=0,rounding_size=0.2',
                       facecolor='#1e293b', edgecolor=DASH_CLR,
                       linewidth=1.2, linestyle='--', zorder=2)
ax.add_patch(rect2)

ax.text(tx + tw/2, ty + th - 0.2, 'TRACEABILITY CHAIN  -  One Finding ID across every artifact',
        ha='center', va='top', fontsize=10, fontweight='bold', color=DASH_CLR)

# chain boxes
chain = [
    (0.7,  'Code File\nPaymentService.cs:47\n(no HTTP timeout)'),
    (4.2,  'Finding P0-001\nEvidence | Risk | Impact\nFile + Line cited'),
    (8.0,  'Master Report\nRemediation roadmap\nP0 priority'),
    (11.8, 'Developer Guide\nC# code example\nPolly retry policy'),
    (15.6, 'Code Assessment\nWAF RE:07 mapped\nActive/Active blocker'),
    (19.4, 'ADO Story\nAcceptance criteria\nLinked to P0-001'),
]
c_colors = [INPUT_BD, GATE_BD, PLAN_BD, PLAN_BD, PLAN_BD, OUT_BD]
for i, ((cx_start, ctxt), cc) in enumerate(zip(chain, c_colors)):
    rx = tx + 0.2 + cx_start * (tw - 0.4) / 21
    ry = ty + 0.35
    rw, rh = 2.9, 1.5
    c_rect = FancyBboxPatch((rx, ry), rw, rh,
                            boxstyle='round,pad=0,rounding_size=0.15',
                            facecolor=BG, edgecolor=cc, linewidth=1.4, zorder=3)
    ax.add_patch(c_rect)
    lines = ctxt.split('\n')
    ax.text(rx + rw/2, ry + rh/2 + 0.15, lines[0],
            ha='center', va='center', fontsize=8, fontweight='bold', color=cc, zorder=4)
    for j, ln in enumerate(lines[1:], 1):
        ax.text(rx + rw/2, ry + rh/2 - 0.12*j,
                ln, ha='center', va='center', fontsize=7, color='#cbd5e1', zorder=4)
    if i < len(chain) - 1:
        ax.annotate('', xy=(rx + rw + 0.1, ry + rh/2),
                    xytext=(rx + rw, ry + rh/2),
                    arrowprops=dict(arrowstyle='->', color=DASH_CLR, lw=1.5), zorder=5)

# ── Legend ──────────────────────────────────────────────────────────────────────
legend_items = [
    (INPUT_BD, 'Repository Input'),
    (RES_BD,   'Task Researcher  (evidence-only · no recommendations)'),
    (GATE_BD,  'Gate / Traceability'),
    (PLAN_BD,  'Task Planner  (remediation · code examples · design)'),
    (OUT_BD,   'ADO Backlog Output'),
]
lx, ly = 0.5, 4.05
for lc, ltxt in legend_items:
    ax.add_patch(FancyBboxPatch((lx, ly - 0.1), 0.22, 0.22,
                 boxstyle='round,pad=0', facecolor=lc+'44', edgecolor=lc,
                 linewidth=1.2, zorder=3))
    ax.text(lx + 0.32, ly + 0.02, ltxt, ha='left', va='center',
            fontsize=7.5, color=TEXT_S, zorder=4)
    lx += 4.3

# ── Footer ─────────────────────────────────────────────────────────────────────
ax.text(11, 0.18, 'HVE Resiliency Research Skill  |  ecommerce-platform  |  West US 2 -> West US Failover Assessment  |  May 2026',
        ha='center', va='center', fontsize=8, color='#475569')

# ── Save ───────────────────────────────────────────────────────────────────────
out = r'c:\Users\ambarana\ecommerce-platform\docs\architecture\resiliency-presentation\exec-diagram.jpg'
fig.savefig(out, dpi=180, bbox_inches='tight', facecolor=BG,
            format='jpeg', pil_kwargs={'quality': 95, 'optimize': True})
print(f'Saved: {out}')
plt.close()

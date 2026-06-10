---
description: platform context and evidence-only rules for resiliency research prompts
applyTo: '.github/prompts/researcher/ospg-hve-researcher-*.prompt.md, .github/prompts/service/ospg-hve-researcher-*.prompt.md'
---

# Platform Context

Apply this context to all  resiliency research prompts.

* Contoso operates applications in Azure
* Validating readiness for zone failure within West US 2
* Validating readiness for full regional failover from West US 2 to West US
* Scope is the current repository within the Digital Payments platform
* HVE Task Researcher rules: evidence only, no remediation, no code examples
* All findings must cite file and line-level evidence
* Classify every finding using the priority framework: P0 (Blocking/Critical), P1 (High Priority), P2 (Improvement/Best Practice), P3 (Non-Blocking Code Consistency)
* Output research artifacts to `.copilot-tracking/research/` and use the repository name as the prefix for all output files (e.g., `<repo-name>-research-output.md`).

## Priority Definitions

* P0: Critical / Blocking. Causes outage, data loss, duplicate charges, or inability to fail over safely during zone or regional failure.
* P1: Required, Non-Blocking. Does not fully block failover but materially increases application risk, data risk, or customer impact during failure.
* P2: Improvement / Best Practice. Does not materially impact correctness during failover but weakens resilience posture or operational clarity.
* P3: Non-Blocking Code Consistency. Captures maintainability, readability, duplication, or inconsistent pattern issues that are non-blocking.

## Service Exclusion Rule

* After Prompts 1a and 1b complete, dependencies classified in Section 2 (Checked But Not Present) and Section 3 (Not Applicable) are excluded from analysis in Prompts 2-7 and service-specific prompts (8-19)
* Prompts 2-7 and service-specific prompts (8-19) analyze only dependencies confirmed as used in Section 1 of the Prompt 1a and 1b outputs

## Next Step Suggestions

After completing each research prompt output, end the response with a next-step suggestion the user can click. Format as:

> **Next step:** Run `/clear`, then `/command-name`

Follow this sequence:

| Current Prompt                      | Next Step                                                                                                               |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `/hve-researcher-0`            | `/hve-researcher-1a`                                                                                               |
| `/hve-researcher-1a`           | `/hve-researcher-1b` (review Section 1 results first)                                                              |
| `/hve-researcher-1b`           | `/hve-researcher-2` (review Section 1 results from 1a and 1b; Sections 2-3 are excluded from here on)              |
| `/hve-researcher-2`            | `/hve-researcher-3`                                                                                                |
| `/hve-researcher-3`            | `/hve-researcher-4`                                                                                                |
| `/hve-researcher-4`            | `/hve-researcher-5`                                                                                                |
| `/hve-researcher-5`            | `/hve-researcher-6`                                                                                                |
| `/hve-researcher-6`            | `/hve-researcher-7-logging`                                                                                        |
| `/hve-researcher-7-logging`    | First applicable service-specific prompt from Phase 2, or `/hve-researcher-consolidate` if none apply              |
| Service-specific prompts (8-19)     | Next applicable service prompt for a Prompt 1 Section 1 dependency, or `/hve-researcher-consolidate` when complete  |
| `/hve-researcher-consolidate`  | `/hve-planner-0`                                                                                                   |

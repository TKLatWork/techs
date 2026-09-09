# Spec-driven rules
This project use Spec/Doc to control and driven/control develeopment

# Terminology
Spec/Doc: natual language docs/info/rules that code based on, source of truth.
Scaffolding: base on the spec/doc, the code structure focused layer and process.
Versioning: change to spec should be record with version, following pattern a.b.c
Spec type: When a spec type is given, it should follow the spec type's template 
Contract code: code used for Scaffolding, Testing/Interface/Abstract class/Models and etc

# Core concept
>3 levels: Spec/Doc, Scaffold code / Contract code / Implement code
>Each layer contain the info they fouces.
>Spec/Doc focuse on high-level desing/domain knowledge.
>Scaffolding focuse on code, but high-level/contracts/code structure patterns
>Implementation code: detail/low-level/unit-single function fouces

# Wording
> Keep wording clean and lean.
> Use Table/List to present informations
> Group/Review/Reword to keep information well grouped, check for repeating/out-date info.

# Scaffolding
>When spec/doc are ready, can move to scaffolding step, after scaffolding code are ready/reviewed, Then move to full implement.
>Serparte Contract code / Implement code

# Spec Templates

## Frontend feature spec template
check [Frontend-feature](./Frontend-feature-spec-template.md)
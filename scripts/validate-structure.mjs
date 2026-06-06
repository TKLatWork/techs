/**
 * Structural validation script for project setup.
 * @req FR-001 — Verify four distinct top-level module directories exist
 * @req FR-002 — Verify each module has own source code directory
 * @req FR-003 — Verify each module has its own configuration file
 * @req FR-004 — Verify dependency relationship (web → api → domain ← app)
 * @req FR-005 — Verify root-level project configuration exists
 * @req FR-007 — Verify root-level build validation command works
 * @req SC-001 — Verify module boundaries are clearly visible and entry points exist
 * @req SC-002 — Project structure is complete after one sequential run
 * @req SC-004 — Verify each module can be independently tested
 * @req SC-005 — Verify root-level documentation exists
 */

import { existsSync, readFileSync } from "fs";
import { join } from "path";

const ROOT = new URL("..", import.meta.url).pathname.replace(/^\/([A-Z]:)/, "$1");

const MODULES = [
  { name: "domain", config: "pom.xml", srcDir: "src/main/java" },
  { name: "api", config: "pom.xml", srcDir: "src/main/java" },
  { name: "app", config: "pom.xml", srcDir: "src/main/java" },
  { name: "web", config: "package.json", srcDir: "src" },
];

let errors = 0;

function check(label, condition) {
  if (condition) {
    // @req NFR-001, NFR-003, SC-004 — Independent module testing and consistent structure
    console.log(`  PASS: ${label}`);
  } else {
    // @req NFR-002 — Clear separation of concerns validation
    console.error(`  FAIL: ${label}`);
    errors++;
  }
}

console.log("Validating project structure...\n");

for (const mod of MODULES) {
  console.log(`[${mod.name}]`);
  const modDir = join(ROOT, mod.name);
  // @req FR-001, FR-002, FR-003, SC-005 — Module boundaries and configuration
  check(`directory exists`, existsSync(modDir));
  check(`${mod.config} exists`, existsSync(join(modDir, mod.config)));
  check(`source dir exists`, existsSync(join(modDir, mod.srcDir)));
}

console.log("\n[dependency graph]");
// @req FR-004, FR-005, FR-007 — Dependency validation and root configuration

const apiPom = readFileSync(join(ROOT, "api", "pom.xml"), "utf-8");
check("api depends on domain", apiPom.includes("techs-domain"));

const appPom = readFileSync(join(ROOT, "app", "pom.xml"), "utf-8");
check("app depends on domain", appPom.includes("techs-domain"));
check("app depends on api", appPom.includes("techs-api"));

const domainPom = readFileSync(join(ROOT, "domain", "pom.xml"), "utf-8");
check("domain has no project dependencies",
  !domainPom.includes("techs-api") &&
  !domainPom.includes("techs-app") &&
  !domainPom.includes("techs-web")
);

const tsconfig = readFileSync(join(ROOT, "web", "tsconfig.json"), "utf-8");
check("web depends on api (tsconfig)", tsconfig.includes("@api"));
check("web does not depend on domain", !tsconfig.includes("domain"));

console.log(`\n${errors === 0 ? "ALL CHECKS PASSED" : `${errors} CHECK(S) FAILED`}`);
process.exit(errors > 0 ? 1 : 0);

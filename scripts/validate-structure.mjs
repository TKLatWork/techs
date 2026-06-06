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
    console.log(`  PASS: ${label}`);
  } else {
    console.error(`  FAIL: ${label}`);
    errors++;
  }
}

console.log("Validating project structure...\n");

for (const mod of MODULES) {
  console.log(`[${mod.name}]`);
  const modDir = join(ROOT, mod.name);
  check(`directory exists`, existsSync(modDir));
  check(`${mod.config} exists`, existsSync(join(modDir, mod.config)));
  check(`source dir exists`, existsSync(join(modDir, mod.srcDir)));
}

console.log("\n[dependency graph]");

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

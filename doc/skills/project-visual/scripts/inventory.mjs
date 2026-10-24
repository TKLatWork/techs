#!/usr/bin/env node
/**
 * Optional inventory helper for project-visual.
 *
 * Walks a scope and records files, classes, functions, route declarations and
 * imports. This is raw material for the architecture JSON, not a finished map:
 * levels, scopes and dependency direction still need human/agent judgment.
 *
 * Usage:
 *   node inventory.mjs <scope-dir> [-o <inventory.json>] [--max-files 1200]
 */
import fs from 'node:fs';
import path from 'node:path';

const LANGUAGE_BY_EXT = {
  '.js': 'javascript', '.jsx': 'javascript', '.mjs': 'javascript', '.cjs': 'javascript',
  '.ts': 'typescript', '.tsx': 'typescript', '.mts': 'typescript', '.cts': 'typescript',
  '.py': 'python', '.java': 'java', '.kt': 'kotlin', '.kts': 'kotlin',
  '.go': 'go', '.rs': 'rust', '.rb': 'ruby', '.php': 'php', '.cs': 'csharp',
  '.scala': 'scala', '.swift': 'swift', '.vue': 'vue', '.svelte': 'svelte',
};

const SKIP_DIRS = new Set([
  '.git', '.hg', '.svn', '.idea', '.vscode', '.cache', '.tmp',
  'node_modules', 'bower_components', 'vendor', 'dist', 'build', 'out',
  'target', 'coverage', '.next', '.nuxt', '.turbo', '.svelte-kit',
  '.venv', 'venv', '__pycache__', '.pytest_cache', '.mypy_cache',
  'bin', 'obj', 'gradle', 'generated',
]);

function usage() {
  console.log(`project-visual inventory

Usage:
  node inventory.mjs <scope-dir> [-o <inventory.json>] [--max-files 1200]
`);
}

function fail(message) {
  console.error(`error: ${message}\n`);
  usage();
  process.exit(1);
}

function parseArgs(argv) {
  const opts = { scope: null, output: null, maxFiles: 1200 };
  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    if (arg === '-h' || arg === '--help') {
      usage();
      process.exit(0);
    } else if (arg === '-o' || arg === '--out') {
      opts.output = argv[i + 1];
      i += 1;
    } else if (arg === '--max-files') {
      const value = Number(argv[i + 1]);
      if (!Number.isFinite(value) || value <= 0) fail('--max-files needs a positive number');
      opts.maxFiles = Math.floor(value);
      i += 1;
    } else if (arg.startsWith('-')) {
      fail(`unknown option: ${arg}`);
    } else if (!opts.scope) {
      opts.scope = arg;
    } else {
      fail(`unexpected argument: ${arg}`);
    }
  }
  if (!opts.scope) fail('missing scope directory');
  return opts;
}

function walk(root, maxFiles) {
  const files = [];
  const stack = [root];

  while (stack.length && files.length < maxFiles) {
    const dir = stack.pop();
    let entries;
    try {
      entries = fs.readdirSync(dir, { withFileTypes: true });
    } catch {
      continue;
    }
    entries.sort((a, b) => a.name.localeCompare(b.name));
    for (const entry of entries) {
      if (files.length >= maxFiles) break;
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        if (SKIP_DIRS.has(entry.name)) continue;
        stack.push(full);
        continue;
      }
      if (!entry.isFile()) continue;
      const language = LANGUAGE_BY_EXT[path.extname(entry.name).toLowerCase()];
      if (language) files.push({ full, language });
    }
  }
  return files;
}

function lineNumberAt(text, index) {
  let line = 1;
  for (let i = 0; i < index; i += 1) {
    if (text.charCodeAt(i) === 10) line += 1;
  }
  return line;
}

function collectUnique(target, values) {
  for (const value of values) {
    if (value && !target.includes(value)) target.push(value);
  }
}

function scanFile(filePath, language, root) {
  const buffer = fs.readFileSync(filePath);
  if (buffer.includes(0)) return null;
  if (buffer.length > 400 * 1024) return null;

  const text = buffer.toString('utf8');
  const classes = [];
  const functions = [];
  const routes = [];
  const imports = [];

  const classPatterns = [
    /\b(?:export\s+)?(?:default\s+)?(?:abstract\s+)?(?:class|interface|enum|record|trait|struct)\s+([A-Za-z_$][\w$]*)/g,
    /^\s*class\s+([A-Za-z_]\w*)/gm,
    /\btype\s+([A-Za-z_]\w*)\s+struct\b/g,
  ];
  for (const pattern of classPatterns) {
    for (const match of text.matchAll(pattern)) classes.push(match[1]);
  }

  const functionPatterns = [
    /\b(?:export\s+)?(?:default\s+)?(?:async\s+)?function\s+([A-Za-z_$][\w$]*)/g,
    /\bexport\s+const\s+([A-Za-z_$][\w$]*)\s*=\s*(?:async\s*)?\(/g,
    /^\s*(?:async\s+)?def\s+([A-Za-z_]\w*)/gm,
    /^func\s+(?:\([^)]*\)\s*)?([A-Za-z_]\w*)/gm,
  ];
  for (const pattern of functionPatterns) {
    for (const match of text.matchAll(pattern)) functions.push(match[1]);
  }

  const routePatterns = [];
  if (language === 'javascript' || language === 'typescript' || language === 'vue' || language === 'svelte') {
    routePatterns.push(
      { pattern: /\b(?:app|router|server|api)\.(get|post|put|patch|delete|use)\s*\(\s*['"`]([^'"`]+)/gi, method: 1, route: 2 },
      { pattern: /@(Get|Post|Put|Patch|Delete)\s*\(\s*['"]([^'"]*)['"]/g, method: 1, route: 2 },
    );
  } else if (language === 'python') {
    routePatterns.push(
      { pattern: /@(?:app|router|bp|blueprint)\.(get|post|put|patch|delete|route)\s*\(\s*['"]([^'"]+)/gi, method: 1, route: 2 },
    );
  } else if (language === 'java' || language === 'kotlin' || language === 'scala') {
    routePatterns.push(
      { pattern: /@(?:Get|Post|Put|Patch|Delete|Request)Mapping\s*\(\s*(?:value\s*=\s*)?['"]([^'"]*)['"]/g, method: 0, route: 1 },
    );
  } else if (language === 'go') {
    routePatterns.push(
      { pattern: /\b(?:HandleFunc|Handle)\s*\(\s*"([^"]+)"/g, method: 0, route: 1 },
    );
  }
  for (const rule of routePatterns) {
    for (const match of text.matchAll(rule.pattern)) {
      const method = rule.method ? String(match[rule.method] || '').toUpperCase() : '';
      const route = String(match[rule.route] || '');
      if (!route) continue;
      routes.push({
        method: method === 'USE' ? 'ANY' : method,
        path: route,
        line: lineNumberAt(text, match.index || 0),
      });
    }
  }

  const importPatterns = [];
  if (language === 'javascript' || language === 'typescript' || language === 'vue' || language === 'svelte') {
    importPatterns.push(/(?:from\s+|require\s*\(\s*)['"]([^'"]+)['"]/g);
  } else if (language === 'python') {
    importPatterns.push(
      /^\s*from\s+([.\w]+)\s+import\b/gm,
      /^\s*import\s+([.\w]+)\b/gm,
    );
  } else if (language === 'java' || language === 'kotlin' || language === 'scala' || language === 'swift' || language === 'csharp') {
    importPatterns.push(/^\s*(?:import|using)\s+([\w.]+)/gm);
  } else if (language === 'go') {
    importPatterns.push(/^\s*(?:import\s+)?(?:\w+\s+)?"([^"]+)"/gm);
  } else if (language === 'rust') {
    importPatterns.push(/^\s*use\s+([\w:]+)/gm);
  }
  for (const pattern of importPatterns) {
    for (const match of text.matchAll(pattern)) imports.push(match[1]);
  }

  const uniqueClasses = [];
  const uniqueFunctions = [];
  const uniqueImports = [];
  collectUnique(uniqueClasses, classes);
  collectUnique(uniqueFunctions, functions);
  collectUnique(uniqueImports, imports);

  const uniqueRoutes = [];
  const seenRoutes = new Set();
  for (const route of routes) {
    const key = `${route.method} ${route.path}`;
    if (seenRoutes.has(key)) continue;
    seenRoutes.add(key);
    uniqueRoutes.push(route);
  }

  return {
    path: path.relative(root, filePath).replace(/\\/g, '/'),
    language,
    bytes: buffer.length,
    lines: text.split('\n').length,
    classes: uniqueClasses,
    functions: uniqueFunctions,
    routes: uniqueRoutes,
    imports: uniqueImports,
  };
}

function main() {
  const opts = parseArgs(process.argv.slice(2));
  const root = path.resolve(opts.scope);
  if (!fs.existsSync(root) || !fs.statSync(root).isDirectory()) {
    fail(`scope is not a directory: ${root}`);
  }

  const candidates = walk(root, opts.maxFiles);
  const files = [];
  for (const candidate of candidates) {
    try {
      const record = scanFile(candidate.full, candidate.language, root);
      if (record) files.push(record);
    } catch {
      // Unreadable file: skip rather than failing the whole inventory.
    }
  }

  files.sort((a, b) => a.path.localeCompare(b.path));
  const byLanguage = {};
  let classCount = 0;
  let functionCount = 0;
  let routeCount = 0;
  let importCount = 0;
  for (const file of files) {
    byLanguage[file.language] = (byLanguage[file.language] || 0) + 1;
    classCount += file.classes.length;
    functionCount += file.functions.length;
    routeCount += file.routes.length;
    importCount += file.imports.length;
  }

  const output = {
    generatedAt: new Date().toISOString(),
    scope: root,
    truncated: candidates.length >= opts.maxFiles,
    files,
    summary: {
      files: files.length,
      byLanguage,
      classes: classCount,
      functions: functionCount,
      routes: routeCount,
      imports: importCount,
    },
  };

  const outputPath = path.resolve(
    opts.output || path.join(process.cwd(), 'project-visual-inventory.json'),
  );
  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
  fs.writeFileSync(outputPath, `${JSON.stringify(output, null, 2)}\n`, 'utf8');

  console.log(`inventory: ${outputPath}`);
  console.log(`  ${files.length} files, ${classCount} classes, ${functionCount} functions, ${routeCount} routes, ${importCount} imports`);
  if (output.truncated) console.log(`  note: stopped at --max-files ${opts.maxFiles}`);
}

main();
#!/usr/bin/env node
/**
 * project-visual renderer
 *
 * Usage:
 *   node render.mjs <architecture.json> [-o <out.html>] [--open] [--title "..."]
 *
 * The JSON must define `levels` inferred from the code scope: names, count and
 * top→bottom ordering are project-specific. This script validates and
 * normalizes the model, computes a deterministic floor-plan layout, and writes
 * one self-contained HTML file (no network dependencies) with data embedded.
 */
import fs from 'node:fs';
import path from 'node:path';
import { spawn } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const TEMPLATE_PATH = path.resolve(__dirname, '..', 'assets', 'viewer.html');

const PALETTE = [
  '#58a6ff', '#3fb950', '#d29922', '#bc8cff', '#39c5cf',
  '#ffa657', '#f85149', '#7ee787', '#ff7b72', '#a5d6ff',
];

const CELL = 44;
const NODE_W = 3.2;
const NODE_H = 2.1;
const GAP = 0.55;
const ZONE_PAD = 1.0;
const HEADER = 1.3;
const ZONE_GAP = 1.5;
const MAX_W = 34;

function usage() {
  console.log(`project-visual renderer

Usage:
  node render.mjs <architecture.json> [-o <out.html>] [--open] [--title "..."]
`);
}

function fail(message) {
  console.error(`error: ${message}\n`);
  usage();
  process.exit(1);
}

function parseArgs(argv) {
  const opts = { input: null, output: null, open: false, title: null };
  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    if (arg === '-h' || arg === '--help') {
      usage();
      process.exit(0);
    } else if (arg === '-o' || arg === '--out') {
      opts.output = argv[i + 1];
      i += 1;
    } else if (arg === '--open') {
      opts.open = true;
    } else if (arg === '--title') {
      opts.title = argv[i + 1];
      i += 1;
    } else if (arg.startsWith('-')) {
      fail(`unknown option: ${arg}`);
    } else if (!opts.input) {
      opts.input = arg;
    } else {
      fail(`unexpected argument: ${arg}`);
    }
  }
  if (!opts.input) fail('missing input JSON path');
  if (opts.output === '') fail('missing value for --out');
  if (opts.title === '') fail('missing value for --title');
  return opts;
}

function isNum(value) {
  return typeof value === 'number' && Number.isFinite(value);
}

function slug(value) {
  return String(value)
    .toLowerCase()
    .normalize('NFKD')
    .replace(/[^\w]+/g, '-')
    .replace(/^-+|-+$/g, '') || 'item';
}

function prettify(value) {
  return String(value)
    .replace(/[-_]+/g, ' ')
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

function asStringArray(value) {
  if (!Array.isArray(value)) return [];
  return value.map((v) => String(v)).filter(Boolean);
}

function normalizeFiles(value) {
  let raw = value;
  if (typeof raw === 'string') raw = [raw];
  if (!Array.isArray(raw)) return [];
  return raw
    .map((file) => {
      if (typeof file === 'string') return { path: file };
      if (!file || typeof file !== 'object') return null;
      const filePath = String(file.path || '');
      if (!filePath) return null;
      const out = { path: filePath };
      if (isNum(file.line) && file.line > 0) out.line = Math.round(file.line);
      return out;
    })
    .filter(Boolean);
}

function normalizeMetrics(value) {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return {};
  const out = Object.create(null);
  for (const [key, raw] of Object.entries(value)) {
    if (isNum(raw)) out[String(key)] = raw;
  }
  return out;
}

function normalize(raw, inputPath) {
  const errors = [];
  const warnings = [];
  const data = raw && typeof raw === 'object' && !Array.isArray(raw) ? raw : {};

  const inputDir = path.dirname(path.resolve(inputPath));
  const project = data.project && typeof data.project === 'object' ? { ...data.project } : {};
  project.name = String(project.name || path.basename(inputDir) || 'Project');
  project.description = project.description ? String(project.description) : '';
  if (!project.root) {
    project.root = process.cwd();
  } else if (!path.isAbsolute(String(project.root))) {
    project.root = path.resolve(inputDir, String(project.root));
  }
  project.root = String(project.root).replace(/[\\/]+$/, '');
  project.generatedAt = project.generatedAt || new Date().toISOString();

  let levels = Array.isArray(data.levels) ? data.levels : [];
  if (!levels.length) {
    errors.push('levels must be inferred from the code scope and provided explicitly (no default levels)');
  }

  levels = levels.map((level, index) => {
    const source = level && typeof level === 'object' ? level : {};
    const label = String(source.label || source.name || `Level ${index + 1}`);
    return {
      id: String(source.id || slug(label)),
      label,
      order: isNum(source.order) ? source.order : index,
      description: String(source.description || ''),
    };
  });
  levels.sort((a, b) => a.order - b.order);

  const levelIds = new Set();
  for (const level of levels) {
    if (!level.id) errors.push('a level has an empty id');
    else if (levelIds.has(level.id)) errors.push(`duplicate level id "${level.id}"`);
    else levelIds.add(level.id);
  }

  const nodeList = Array.isArray(data.nodes) ? data.nodes : [];
  if (!nodeList.length) errors.push('nodes must be a non-empty array');

  const seenNodeIds = new Set();
  const nodes = nodeList.map((rawNode, index) => {
    const source = rawNode && typeof rawNode === 'object' ? rawNode : {};
    let id = String(source.id || slug(`${source.level || 'node'}-${source.label || index + 1}`));
    if (seenNodeIds.has(id)) {
      const base = id;
      let suffix = 2;
      while (seenNodeIds.has(`${base}-${suffix}`)) suffix += 1;
      id = `${base}-${suffix}`;
      warnings.push(`nodes[${index}].id "${base}" was duplicated; renamed to "${id}"`);
    }
    seenNodeIds.add(id);

    const level = String(source.level || '');
    if (!levelIds.has(level)) {
      errors.push(`nodes[${index}].level "${level}" does not match a level id`);
    }

    const files = normalizeFiles(source.files ?? source.file);
    const subtitle = String(
      source.subtitle
      || (files[0] ? path.basename(files[0].path) : '')
    );

    return {
      id,
      level,
      scope: source.scope == null ? '' : String(source.scope),
      label: String(source.label || source.name || id),
      subtitle,
      kind: String(source.kind || 'node'),
      description: String(source.description || ''),
      files,
      symbols: asStringArray(source.symbols),
      tags: asStringArray(source.tags),
      metrics: normalizeMetrics(source.metrics),
      layout: source.layout && typeof source.layout === 'object' ? source.layout : null,
    };
  });

  let scopes = Array.isArray(data.scopes) ? data.scopes : [];
  scopes = scopes.map((scope, index) => {
    const source = scope && typeof scope === 'object' ? scope : {};
    const label = String(source.label || source.name || source.id || `Scope ${index + 1}`);
    return {
      id: String(source.id || slug(label)),
      label,
      description: String(source.description || ''),
      color: /^#[0-9a-f]{6}$/i.test(String(source.color || ''))
        ? String(source.color)
        : PALETTE[index % PALETTE.length],
    };
  });

  if (!scopes.length) {
    const derived = [];
    for (const node of nodes) {
      if (node.scope && !derived.includes(node.scope)) derived.push(node.scope);
    }
    if (!derived.length) derived.push('workspace');
    scopes = derived.map((id, index) => ({
      id,
      label: prettify(id),
      description: '',
      color: PALETTE[index % PALETTE.length],
    }));
  }

  const scopeIds = new Set();
  for (const scope of scopes) {
    if (scopeIds.has(scope.id)) {
      errors.push(`duplicate scope id "${scope.id}"`);
    } else {
      scopeIds.add(scope.id);
      if (!/^#[0-9a-f]{6}$/i.test(scope.color)) scope.color = PALETTE[scopeIds.size % PALETTE.length];
    }
  }

  for (let index = 0; index < nodes.length; index += 1) {
    const node = nodes[index];
    if (!node.scope) {
      node.scope = scopes[0]?.id || 'workspace';
      warnings.push(`nodes[${index}] has no scope; assigned "${node.scope}"`);
    } else if (!scopeIds.has(node.scope)) {
      const scope = {
        id: node.scope,
        label: prettify(node.scope),
        description: '',
        color: PALETTE[scopeIds.size % PALETTE.length],
      };
      scopes.push(scope);
      scopeIds.add(scope.id);
      warnings.push(`nodes[${index}].scope "${node.scope}" was not declared; added it`);
    }
  }

  const nodeById = new Map(nodes.map((node) => [node.id, node]));
  const rawEdges = Array.isArray(data.edges) ? data.edges : [];
  const edges = [];
  const seenEdgeIds = new Set();
  const seenEdgeKeys = new Set();
  rawEdges.forEach((rawEdge, index) => {
    if (!rawEdge || typeof rawEdge !== 'object') return;
    const from = String(rawEdge.from || '');
    const to = String(rawEdge.to || '');
    if (!nodeById.has(from)) {
      errors.push(`edges[${index}].from "${from}" is not a node id`);
      return;
    }
    if (!nodeById.has(to)) {
      errors.push(`edges[${index}].to "${to}" is not a node id`);
      return;
    }
    if (from === to) {
      warnings.push(`edges[${index}] is a self-reference and was skipped`);
      return;
    }
    const kind = String(rawEdge.kind || 'depends');
    const key = `${from}|${to}|${kind}`;
    if (seenEdgeKeys.has(key)) {
      warnings.push(`duplicate edge ${from} -> ${to} (${kind}) skipped`);
      return;
    }
    seenEdgeKeys.add(key);
    let id = String(rawEdge.id || `${from}--${to}--${kind}`);
    while (seenEdgeIds.has(id)) id = `${id}-x`;
    seenEdgeIds.add(id);
    edges.push({
      id,
      from,
      to,
      kind,
      label: String(rawEdge.label || ''),
    });
  });

  const scopeOrder = new Map(scopes.map((scope, index) => [scope.id, index]));
  const floors = Object.create(null);

  for (const level of levels) {
    const levelNodes = nodes.filter((node) => node.level === level.id);
    const layout = layoutLevel(level, levelNodes, scopes, scopeOrder, warnings);
    floors[level.id] = {
      width: layout.width,
      height: layout.height,
      zones: layout.zones,
      nodeCount: levelNodes.length,
    };
  }

  for (const node of nodes) {
    if (!node.files.length) warnings.push(`node "${node.id}" has no file evidence`);
  }
  for (const scope of scopes) {
    if (!nodes.some((node) => node.scope === scope.id)) {
      warnings.push(`scope "${scope.id}" has no nodes`);
    }
  }
  for (const level of levels) {
    if (!level.description) {
      warnings.push(`level "${level.id}" has no description; record the code evidence for this tier`);
    }
    if (!nodes.some((node) => node.level === level.id)) {
      warnings.push(`level "${level.id}" has no nodes`);
    }
  }

  return {
    data: {
      schemaVersion: String(data.schemaVersion || '1.0'),
      project,
      levels,
      scopes,
      nodes,
      edges,
      view: {
        generator: 'project-visual',
        cell: CELL,
        floors,
      },
    },
    errors,
    warnings,
  };
}

function layoutLevel(level, levelNodes, scopes, scopeOrder, warnings) {
  if (!levelNodes.length) {
    return { width: 12, height: 8, zones: [] };
  }

  const explicit = levelNodes.every(
    (node) => node.layout && isNum(node.layout.x) && isNum(node.layout.y),
  );
  const partiallyLaidOut = levelNodes.some((node) => node.layout);

  if (explicit) {
    return explicitLayout(levelNodes, scopes, scopeOrder);
  }
  if (partiallyLaidOut) {
    warnings.push(`level "${level.id}": mixed layout data; auto-layout applied to every block`);
  }
  return autoLayout(levelNodes, scopes, scopeOrder);
}

function groupByScope(levelNodes, scopes, scopeOrder) {
  const groups = scopes
    .map((scope) => ({
      scope,
      nodes: levelNodes
        .filter((node) => node.scope === scope.id)
        .sort((a, b) => a.label.localeCompare(b.label)),
    }))
    .filter((group) => group.nodes.length > 0);

  const known = new Set(groups.map((group) => group.scope.id));
  const extra = levelNodes
    .filter((node) => !known.has(node.scope))
    .sort((a, b) => a.label.localeCompare(b.label));
  if (extra.length) {
    groups.push({
      scope: { id: extra[0].scope, label: prettify(extra[0].scope), color: PALETTE[0] },
      nodes: extra,
    });
  }
  groups.sort((a, b) => (scopeOrder.get(a.scope.id) ?? 999) - (scopeOrder.get(b.scope.id) ?? 999));
  return groups;
}

function autoLayout(levelNodes, scopes, scopeOrder) {
  const groups = groupByScope(levelNodes, scopes, scopeOrder);
  const zones = [];
  let cursorX = 1;
  let cursorY = 1;
  let rowHeight = 0;

  for (const group of groups) {
    const count = group.nodes.length;
    const cols = Math.max(1, Math.min(4, Math.ceil(Math.sqrt(count))));
    const rows = Math.ceil(count / cols);
    const zoneW = ZONE_PAD * 2 + cols * NODE_W + (cols - 1) * GAP;
    const zoneH = ZONE_PAD * 2 + HEADER + rows * NODE_H + (rows - 1) * GAP;

    if (cursorX > 1 && cursorX + zoneW > MAX_W) {
      cursorX = 1;
      cursorY += rowHeight + ZONE_GAP;
      rowHeight = 0;
    }

    zones.push({
      scope: group.scope.id,
      x: cursorX,
      y: cursorY,
      w: zoneW,
      h: zoneH,
      count,
    });

    group.nodes.forEach((node, index) => {
      const col = index % cols;
      const row = Math.floor(index / cols);
      node.layout = {
        x: cursorX + ZONE_PAD + col * (NODE_W + GAP),
        y: cursorY + ZONE_PAD + HEADER + row * (NODE_H + GAP),
        w: NODE_W,
        h: NODE_H,
      };
    });

    cursorX += zoneW + ZONE_GAP;
    rowHeight = Math.max(rowHeight, zoneH);
  }

  return finishZones(levelNodes, zones);
}

function explicitLayout(levelNodes, scopes, scopeOrder) {
  for (const node of levelNodes) {
    node.layout = {
      x: node.layout.x,
      y: node.layout.y,
      w: isNum(node.layout.w) ? node.layout.w : NODE_W,
      h: isNum(node.layout.h) ? node.layout.h : NODE_H,
    };
  }

  const groups = groupByScope(levelNodes, scopes, scopeOrder);
  const zones = groups.map((group) => {
    const minX = Math.min(...group.nodes.map((node) => node.layout.x));
    const minY = Math.min(...group.nodes.map((node) => node.layout.y));
    const maxX = Math.max(...group.nodes.map((node) => node.layout.x + node.layout.w));
    const maxY = Math.max(...group.nodes.map((node) => node.layout.y + node.layout.h));
    return {
      scope: group.scope.id,
      x: minX - ZONE_PAD,
      y: minY - HEADER - ZONE_PAD * 0.6,
      w: maxX - minX + ZONE_PAD * 2,
      h: maxY - minY + HEADER + ZONE_PAD * 1.6,
      count: group.nodes.length,
    };
  });

  return finishZones(levelNodes, zones);
}

function finishZones(levelNodes, zones) {
  let dx = 0;
  let dy = 0;
  if (zones.length) {
    const minX = Math.min(...zones.map((zone) => zone.x));
    const minY = Math.min(...zones.map((zone) => zone.y));
    if (minX < 1) dx = 1 - minX;
    if (minY < 1) dy = 1 - minY;
  }

  if (dx || dy) {
    for (const node of levelNodes) {
      node.layout.x += dx;
      node.layout.y += dy;
    }
    for (const zone of zones) {
      zone.x += dx;
      zone.y += dy;
    }
  }

  const width = zones.length
    ? Math.max(12, Math.max(...zones.map((zone) => zone.x + zone.w)) + 2)
    : 12;
  const height = zones.length
    ? Math.max(8, Math.max(...zones.map((zone) => zone.y + zone.h)) + 2)
    : 8;

  return { width, height, zones };
}

function openFile(file) {
  try {
    if (process.platform === 'win32') {
      const child = spawn('explorer.exe', [file], { detached: true, stdio: 'ignore' });
      child.unref();
    } else {
      const command = process.platform === 'darwin' ? 'open' : 'xdg-open';
      const child = spawn(command, [file], { detached: true, stdio: 'ignore' });
      child.unref();
    }
  } catch {
    console.error(`warning: could not open the browser; open this file manually: ${file}`);
  }
}

function main() {
  const opts = parseArgs(process.argv.slice(2));
  const inputPath = path.resolve(opts.input);

  if (!fs.existsSync(inputPath)) fail(`input file not found: ${inputPath}`);
  if (!fs.existsSync(TEMPLATE_PATH)) fail(`viewer template not found: ${TEMPLATE_PATH}`);

  let raw;
  try {
    raw = JSON.parse(fs.readFileSync(inputPath, 'utf8').replace(/^\uFEFF/, ''));
  } catch (error) {
    fail(`could not parse ${inputPath}: ${error.message}`);
  }

  const { data, errors, warnings } = normalize(raw, inputPath);

  if (opts.title) data.project.name = opts.title;

  if (errors.length) {
    console.error(`error: architecture JSON has ${errors.length} problem(s):`);
    for (const error of errors.slice(0, 60)) console.error(`  - ${error}`);
    if (errors.length > 60) console.error(`  ... ${errors.length - 60} more`);
    process.exit(1);
  }

  const template = fs.readFileSync(TEMPLATE_PATH, 'utf8');
  if (!template.includes('__PV_DATA__')) {
    fail('viewer template is missing the __PV_DATA__ placeholder');
  }

  const payload = JSON.stringify(data)
    .replace(/</g, '\\u003c')
    .replace(/>/g, '\\u003e')
    .replace(/&/g, '\\u0026')
    .replace(/\u2028/g, '\\u2028')
    .replace(/\u2029/g, '\\u2029');

  const html = template.replace('__PV_DATA__', () => payload);
  const base = path.basename(opts.input, path.extname(opts.input));
  const outputPath = path.resolve(opts.output || path.join(process.cwd(), `${base}.map.html`));

  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
  fs.writeFileSync(outputPath, html, 'utf8');

  const levelSummary = data.levels
    .map((level) => `${level.label} ${data.view.floors[level.id]?.nodeCount ?? 0}`)
    .join(' / ');

  console.log(`rendered: ${outputPath}`);
  console.log(`  ${data.nodes.length} blocks, ${data.edges.length} links, ${data.levels.length} levels, ${data.scopes.length} scopes`);
  console.log(`  per level: ${levelSummary}`);

  if (warnings.length) {
    console.log(`  warnings (${warnings.length}):`);
    for (const warning of warnings.slice(0, 40)) console.log(`    - ${warning}`);
    if (warnings.length > 40) console.log(`    ... ${warnings.length - 40} more`);
  }

  if (opts.open) openFile(outputPath);
}

main();
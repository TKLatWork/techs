export interface NavItem {
  label: string
  path: string
}

// Feature entries shown in the shared NavBar; paths are relative to the /web base
export const navItems: NavItem[] = [
  { label: 'Timezone', path: '/timezone' },
]

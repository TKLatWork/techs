import { useState } from "react";
import { Box, Typography, Tabs, Tab } from "@mui/material";
import { RoleManager } from "../features/permissions/components/RoleManager";
import { RightManager } from "../features/permissions/components/RightManager";
import { UserManager } from "../features/permissions/components/UserManager";
import { FeatureManager } from "../features/permissions/components/FeatureManager";

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box sx={{ py: 2 }}>{children}</Box> : null;
}

export default function PermissionsPage() {
  const [tab, setTab] = useState(0);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h2" sx={{ mb: 2 }}>Permission Management</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)}>
        <Tab label="Roles" />
        <Tab label="Rights" />
        <Tab label="Users" />
        <Tab label="Features" />
      </Tabs>
      <TabPanel value={tab} index={0}><RoleManager /></TabPanel>
      <TabPanel value={tab} index={1}><RightManager /></TabPanel>
      <TabPanel value={tab} index={2}><UserManager /></TabPanel>
      <TabPanel value={tab} index={3}><FeatureManager /></TabPanel>
    </Box>
  );
}

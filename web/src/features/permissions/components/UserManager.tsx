import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Select, MenuItem, Chip
} from "@mui/material";
import { permissionService } from "../services/permissionService";
import type { RoleDto, UserPermissionsDto } from "../types/permissions";

export function UserManager() {
  const [users, setUsers] = useState<UserPermissionsDto[]>([]);
  const [roles, setRoles] = useState<RoleDto[]>([]);

  const loadData = async () => {
    const [usersRes, rolesRes] = await Promise.all([
      permissionService.listUsers(),
      permissionService.listRoles(),
    ]);
    setUsers(usersRes.data);
    setRoles(rolesRes.data);
  };

  useEffect(() => { loadData(); }, []);

  const handleAssignRole = async (userId: string, roleId: string) => {
    await permissionService.assignRole(userId, roleId);
    loadData();
  };

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Username</TableCell>
            <TableCell>Display Name</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Effective Rights</TableCell>
            <TableCell>Individual Rights</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.userId}>
              <TableCell>{user.username}</TableCell>
              <TableCell>{user.displayName}</TableCell>
              <TableCell>
                <Select
                  value={user.role?.id || ""}
                  size="small"
                  onChange={(e) => handleAssignRole(user.userId, e.target.value)}
                >
                  {roles.map((role) => (
                    <MenuItem key={role.id} value={role.id}>{role.name}</MenuItem>
                  ))}
                </Select>
              </TableCell>
              <TableCell>
                {user.effectiveRights.map((r) => (
                  <Chip key={r.id} label={r.name} size="small" sx={{ m: 0.25 }} />
                ))}
              </TableCell>
              <TableCell>
                {user.individualRights.map((r) => (
                  <Chip key={r.id} label={r.name} size="small" color="secondary" sx={{ m: 0.25 }} />
                ))}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

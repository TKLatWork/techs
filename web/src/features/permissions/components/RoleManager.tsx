import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, TextField, Dialog, DialogTitle, DialogContent, DialogActions,
  Checkbox, Typography, Chip
} from "@mui/material";
import { permissionService } from "../services/permissionService";
import type { RoleDto, RightDto } from "../types/permissions";

export function RoleManager() {
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [rights, setRights] = useState<RightDto[]>([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editRole, setEditRole] = useState<RoleDto | null>(null);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [selectedRightIds, setSelectedRightIds] = useState<Set<string>>(new Set());

  const loadData = async () => {
    const [rolesRes, rightsRes] = await Promise.all([
      permissionService.listRoles(),
      permissionService.listRights(),
    ]);
    setRoles(rolesRes.data);
    setRights(rightsRes.data);
  };

  useEffect(() => { loadData(); }, []);

  const handleCreate = () => {
    setEditRole(null);
    setName("");
    setDescription("");
    setSelectedRightIds(new Set());
    setDialogOpen(true);
  };

  const handleEdit = (role: RoleDto) => {
    setEditRole(role);
    setName(role.name);
    setDescription(role.description || "");
    setSelectedRightIds(new Set(role.rightIds));
    setDialogOpen(true);
  };

  const handleSave = async () => {
    const data = { name, description, rightIds: Array.from(selectedRightIds) };
    if (editRole) {
      await permissionService.updateRole(editRole.id, data);
    } else {
      await permissionService.createRole(data);
    }
    setDialogOpen(false);
    loadData();
  };

  const handleDelete = async (roleId: string) => {
    await permissionService.deleteRole(roleId);
    loadData();
  };

  const toggleRight = (rightId: string) => {
    const next = new Set(selectedRightIds);
    if (next.has(rightId)) next.delete(rightId);
    else next.add(rightId);
    setSelectedRightIds(next);
  };

  return (
    <>
      <Button variant="contained" onClick={handleCreate} sx={{ mb: 2 }}>Create Role</Button>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Description</TableCell>
              <TableCell>Rights</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {roles.map((role) => (
              <TableRow key={role.id}>
                <TableCell>{role.name}</TableCell>
                <TableCell>{role.description}</TableCell>
                <TableCell>{role.rightIds.length}</TableCell>
                <TableCell>
                  {role.builtIn && <Chip label="Built-in" size="small" />}
                  {role.immutable && <Chip label="Immutable" size="small" color="warning" />}
                </TableCell>
                <TableCell>
                  {!role.immutable && (
                    <>
                      <Button size="small" onClick={() => handleEdit(role)}>Edit</Button>
                      {!role.builtIn && (
                        <Button size="small" color="error" onClick={() => handleDelete(role.id)}>Delete</Button>
                      )}
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>{editRole ? "Edit Role" : "Create Role"}</DialogTitle>
        <DialogContent>
          <TextField label="Name" value={name} onChange={(e) => setName(e.target.value)} fullWidth margin="normal" />
          <TextField label="Description" value={description} onChange={(e) => setDescription(e.target.value)} fullWidth margin="normal" />
          <Typography variant="subtitle1" sx={{ mt: 2 }}>Rights</Typography>
          {rights.map((right) => (
            <div key={right.id}>
              <Checkbox
                checked={selectedRightIds.has(right.id)}
                onChange={() => toggleRight(right.id)}
              />
              {right.name} ({right.type})
            </div>
          ))}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSave} variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}

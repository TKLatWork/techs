import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, Chip
} from "@mui/material";
import { permissionService } from "../services/permissionService";
import type { RightDto } from "../types/permissions";

export function RightManager() {
  const [rights, setRights] = useState<RightDto[]>([]);

  const loadData = async () => {
    const { data } = await permissionService.listRights();
    setRights(data);
  };

  useEffect(() => { loadData(); }, []);

  const handleDelete = async (rightId: string) => {
    await permissionService.deleteRight(rightId);
    loadData();
  };

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Type</TableCell>
            <TableCell>URL Pattern</TableCell>
            <TableCell>Action</TableCell>
            <TableCell>Source</TableCell>
            <TableCell>Feature</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rights.map((right) => (
            <TableRow key={right.id}>
              <TableCell>{right.name}</TableCell>
              <TableCell><Chip label={right.type} size="small" /></TableCell>
              <TableCell>{right.urlPattern || "-"}</TableCell>
              <TableCell>{right.action || "-"}</TableCell>
              <TableCell>{right.source}</TableCell>
              <TableCell>{right.featureId || "-"}</TableCell>
              <TableCell>
                {!right.source?.includes("BUILT_IN") && (
                  <Button size="small" color="error" onClick={() => handleDelete(right.id)}>Delete</Button>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

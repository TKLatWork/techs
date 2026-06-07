import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button
} from "@mui/material";
import { permissionService, type FeatureInfo } from "../services/permissionService";

export function FeatureManager() {
  const [features, setFeatures] = useState<FeatureInfo[]>([]);

  const loadData = async () => {
    const { data } = await permissionService.listFeatures();
    setFeatures(data);
  };

  useEffect(() => { loadData(); }, []);

  const handleDisable = async (featureId: string) => {
    await permissionService.disableFeature(featureId);
    loadData();
  };

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Feature ID</TableCell>
            <TableCell>Registered Rights</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {features.map((feature) => (
            <TableRow key={feature.featureId}>
              <TableCell>{feature.featureId}</TableCell>
              <TableCell>{feature.rightCount}</TableCell>
              <TableCell>
                <Button size="small" color="error" onClick={() => handleDisable(feature.featureId)}>
                  Disable
                </Button>
              </TableCell>
            </TableRow>
          ))}
          {features.length === 0 && (
            <TableRow>
              <TableCell colSpan={3}>No features have registered permissions.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

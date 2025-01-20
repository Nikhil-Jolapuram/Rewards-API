import React, { useState } from "react";
import {
  Box,
  TextField,
  Button,
  Typography,
  Card,
  CardContent,
  Grid,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  Stack,
} from "@mui/material";
import { Star, CalendarMonth, Person } from "@mui/icons-material";
import axios from "axios";

const App = () => {
  const [customerId, setCustomerId] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [customerIdError, setCustomerIdError] = useState("");
  const [dateError, setDateError] = useState("");

  const validateInputs = () => {
    let valid = true;

    // Validate Customer ID
    if (!customerId || isNaN(customerId) || customerId <= 0) {
      setCustomerIdError("Customer ID must be a valid positive number.");
      valid = false;
    } else {
      setCustomerIdError("");
    }

    // Validate Dates
    if (!startDate || !endDate) {
      setDateError("Both start and end dates are required.");
      valid = false;
    } else if (new Date(startDate) >= new Date(endDate)) {
      setDateError("End date must be later than start date.");
      valid = false;
    } else {
      setDateError("");
    }

    return valid;
  };

  const fetchRewards = async () => {
    if (!validateInputs()) return;

    setLoading(true);
    setError("");
    setResponse(null);
    try {
      const res = await axios.get("http://localhost:8080/api/rewards", {
        params: { customerId, startDate, endDate },
      });
      setResponse(res.data);
    } catch (err) {
      setError("Failed to fetch rewards. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f5f5f5",
        padding: 4,
      }}
    >
      <Card sx={{ maxWidth: 600, width: "100%", boxShadow: 3 }}>
        <CardContent>
          <Typography variant="h4" gutterBottom align="center">
            Rewards Calculator
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Customer ID"
                variant="outlined"
                value={customerId}
                onChange={(e) => setCustomerId(e.target.value)}
                error={Boolean(customerIdError)}
                helperText={customerIdError}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Start Date"
                type="datetime-local"
                variant="outlined"
                InputLabelProps={{ shrink: true }}
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                error={Boolean(dateError)}
                helperText={dateError}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="End Date"
                type="datetime-local"
                variant="outlined"
                InputLabelProps={{ shrink: true }}
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                error={Boolean(dateError)}
                helperText={dateError}
              />
            </Grid>
            <Grid item xs={12}>
              <Button
                fullWidth
                variant="contained"
                color="primary"
                onClick={fetchRewards}
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : "Get Rewards"}
              </Button>
            </Grid>
          </Grid>

          {error && (
            <Box mt={3}>
              <Alert severity="error">{error}</Alert>
            </Box>
          )}

          {response && (
            <Box mt={3}>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" alignItems="center" spacing={2}>
                    <Avatar sx={{ bgcolor: "primary.main", width: 56, height: 56 }}>
                      <Person fontSize="large" />
                    </Avatar>
                    <Typography variant="h5" gutterBottom>
                      Rewards Summary
                    </Typography>
                  </Stack>
                  <Typography>
                    <strong>Customer ID:</strong> {response.customerId}
                  </Typography>
                  <Typography sx={{ display: "flex", alignItems: "center" }}>
                    <Star color="secondary" sx={{ mr: 1 }} />
                    <strong>Total Points:</strong> {response.totalPoints}
                  </Typography>

                  <Typography variant="h6" mt={2} sx={{ display: "flex", alignItems: "center" }}>
                    <CalendarMonth color="primary" sx={{ mr: 1 }} />
                    Monthly Breakdown:
                  </Typography>
                  <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Month</TableCell>
                          <TableCell align="right">Points</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {Object.entries(response.rewardsSummary).map(
                          ([month, points]) => (
                            <TableRow key={month}>
                              <TableCell component="th" scope="row">
                                {month}
                              </TableCell>
                              <TableCell align="right">{points}</TableCell>
                            </TableRow>
                          )
                        )}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </CardContent>
              </Card>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default App;

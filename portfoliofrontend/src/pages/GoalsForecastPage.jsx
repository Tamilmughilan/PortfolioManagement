import React, { useEffect, useState } from 'react';
import { Target, Plus, Trash2, Sparkles } from 'lucide-react';
import {
  addGoal,
  deleteGoal,
  getGoalForecast,
  getGoals,
  runWhatIf
} from '../services/api';
import SectionHeader from '../components/reactbits/SectionHeader';
import GlowCard from '../components/reactbits/GlowCard';
import StatCard from '../components/reactbits/StatCard';
import '../styles/GoalsForecast.css';

const GoalsForecastPage = ({ portfolioId }) => {
  const [goals, setGoals] = useState([]);
  const [selectedGoal, setSelectedGoal] = useState(null);
  const [forecast, setForecast] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [goalForm, setGoalForm] = useState({
    goalName: '',
    targetAmount: '',
    targetDate: '',
    expectedAnnualReturn: '8'
  });
  const [whatIf, setWhatIf] = useState({
    monthlyContribution: '200',
    expectedAnnualReturn: '8',
    months: '24'
  });
  const [whatIfResult, setWhatIfResult] = useState(null);

  const loadGoals = async () => {
    if (!portfolioId) return;
    try {
      setLoading(true);
      const response = await getGoals(portfolioId);
      setGoals(response.data || []);
      if (response.data?.length) {
        setSelectedGoal(response.data[0]);
      }
      setError('');
    } catch (err) {
      setError('Unable to load goals.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGoals();
  }, [portfolioId]);

  useEffect(() => {
    const loadForecast = async () => {
      if (!selectedGoal || !portfolioId) return;
      try {
        const response = await getGoalForecast(portfolioId, selectedGoal.goalId);
        setForecast(response.data);
      } catch (err) {
        setForecast(null);
      }
    };
    loadForecast();
  }, [selectedGoal, portfolioId]);

  const handleGoalChange = (e) => {
    const { name, value } = e.target;
    setGoalForm(prev => ({ ...prev, [name]: value }));
  };

  const handleWhatIfChange = (e) => {
    const { name, value } = e.target;
    setWhatIf(prev => ({ ...prev, [name]: value }));
  };

  const handleCreateGoal = async (e) => {
    e.preventDefault();
    if (!goalForm.goalName || !goalForm.targetAmount || !goalForm.targetDate) {
      setError('Please complete the goal form.');
      return;
    }
    try {
      await addGoal(portfolioId, {
        goalName: goalForm.goalName,
        targetAmount: Number(goalForm.targetAmount),
        targetDate: goalForm.targetDate,
        expectedAnnualReturn: Number(goalForm.expectedAnnualReturn)
      });
      setGoalForm({ goalName: '', targetAmount: '', targetDate: '', expectedAnnualReturn: '8' });
      loadGoals();
    } catch (err) {
      setError('Unable to create goal.');
    }
  };

  const handleDeleteGoal = async (goalId) => {
    try {
      await deleteGoal(portfolioId, goalId);
      loadGoals();
    } catch (err) {
      setError('Unable to delete goal.');
    }
  };

  const handleRunWhatIf = async (e) => {
    e.preventDefault();
    try {
      const response = await runWhatIf(portfolioId, {
        monthlyContribution: Number(whatIf.monthlyContribution),
        expectedAnnualReturn: Number(whatIf.expectedAnnualReturn),
        months: Number(whatIf.months)
      });
      setWhatIfResult(response.data);
    } catch (err) {
      setError('Unable to run what-if simulation.');
    }
  };

  return (
    <div className="goals-page">
      <SectionHeader
        title="Goals & Forecasting"
        subtitle="Set your targets, see projections, and simulate scenarios."
        icon={<Target size={22} />}
      />

      {error && <div className="goals-error">{error}</div>}

      <div className="goals-grid">
        <GlowCard className="goals-card">
          <h2>Create Goal</h2>
          <form onSubmit={handleCreateGoal} className="goals-form">
            <div className="goals-field">
              <label>Goal Name</label>
              <input
                name="goalName"
                value={goalForm.goalName}
                onChange={handleGoalChange}
                placeholder="e.g., Dream Home"
              />
            </div>
            <div className="goals-field">
              <label>Target Amount</label>
              <input
                name="targetAmount"
                type="number"
                value={goalForm.targetAmount}
                onChange={handleGoalChange}
              />
            </div>
            <div className="goals-field">
              <label>Target Date</label>
              <input
                name="targetDate"
                type="date"
                value={goalForm.targetDate}
                onChange={handleGoalChange}
              />
            </div>
            <div className="goals-field">
              <label>Expected Return (%)</label>
              <input
                name="expectedAnnualReturn"
                type="number"
                value={goalForm.expectedAnnualReturn}
                onChange={handleGoalChange}
              />
            </div>
            <button type="submit" className="goals-submit">
              <Plus size={16} /> Add Goal
            </button>
          </form>
        </GlowCard>

        <GlowCard className="goals-card">
          <h2>Your Goals</h2>
          {loading ? (
            <p>Loading goals...</p>
          ) : goals.length === 0 ? (
            <p>No goals yet. Add one to start forecasting.</p>
          ) : (
            <div className="goals-list">
              {goals.map(goal => (
                <div
                  key={goal.goalId}
                  className={`goal-item ${selectedGoal?.goalId === goal.goalId ? 'active' : ''}`}
                  onClick={() => setSelectedGoal(goal)}
                >
                  <div>
                    <h4>{goal.goalName}</h4>
                    <span>{goal.targetAmount} by {goal.targetDate}</span>
                  </div>
                  <button type="button" onClick={() => handleDeleteGoal(goal.goalId)}>
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>
          )}
        </GlowCard>
      </div>

      {forecast && (
        <div className="forecast-grid">
          <StatCard
            label="Current Value"
            value={`${forecast.currentValue?.toFixed(2)}`}
            subtext="Starting point"
            icon={<Sparkles size={18} />}
          />
          <StatCard
            label="Target Amount"
            value={`${forecast.targetAmount?.toFixed(2)}`}
            subtext={`Due by ${forecast.targetDate}`}
            icon={<Target size={18} />}
          />
          <StatCard
            label="Required Monthly"
            value={`${forecast.requiredMonthlyContribution?.toFixed(2)}`}
            subtext={`${forecast.monthsRemaining} months left`}
            icon={<Sparkles size={18} />}
          />
        </div>
      )}

      {forecast && (
        <GlowCard className="forecast-card">
          <h2>Forecast Trajectory</h2>
          {forecast.trajectory && forecast.trajectory.length > 0 && (
            <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px', marginBottom: '1rem' }}>
              <svg viewBox="0 0 600 250" style={{ width: '100%', height: '250px' }}>
                {(() => {
                  const trajectory = forecast.trajectory || [];
                  if (trajectory.length === 0) return null;
                  
                  const values = trajectory.map(p => Number(p.value || 0));
                  const minValue = Math.min(...values, Number(forecast.currentValue || 0), Number(forecast.targetAmount || 0));
                  const maxValue = Math.max(...values, Number(forecast.currentValue || 0), Number(forecast.targetAmount || 0));
                  const range = maxValue - minValue || 1;
                  const padding = 40;
                  const width = 600 - (padding * 2);
                  const height = 250 - (padding * 2);
                  
                  const points = trajectory.map((point, index) => {
                    const x = padding + (index / Math.max(trajectory.length - 1, 1)) * width;
                    const y = padding + height - ((Number(point.value || 0) - minValue) / range) * height;
                    return { x, y, value: Number(point.value || 0), date: point.date };
                  });
                  
                  const pathData = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
                  const targetY = padding + height - ((Number(forecast.targetAmount || 0) - minValue) / range) * height;
                  
                  return (
                    <>
                      <line x1={padding} y1={padding} x2={padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                      <line x1={padding} y1={padding + height} x2={width + padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                      <line x1={padding} y1={targetY} x2={width + padding} y2={targetY} stroke="#10b981" strokeWidth="2" strokeDasharray="5,5" opacity="0.6" />
                      <text x={width + padding + 5} y={targetY + 4} fontSize="10" fill="#10b981">Target</text>
                      <path d={pathData} fill="none" stroke="#3b82f6" strokeWidth="3" strokeLinecap="round" />
                      {points.map((point, index) => (
                        <circle key={index} cx={point.x} cy={point.y} r="3" fill="#3b82f6" />
                      ))}
                      <text x={padding + width / 2} y={height + padding + 25} textAnchor="middle" fontSize="11" fill="var(--text-light)">Timeline</text>
                      <text x="15" y={padding + height / 2} textAnchor="middle" fontSize="11" fill="var(--text-light)" transform={`rotate(-90 15 ${padding + height / 2})`}>Value</text>
                    </>
                  );
                })()}
              </svg>
            </div>
          )}
          <h3>Forecast Story</h3>
          <p>{forecast.narrative}</p>
        </GlowCard>
      )}

      <GlowCard className="whatif-card">
        <h2>What-If Simulator</h2>
        <form onSubmit={handleRunWhatIf} className="whatif-form">
          <div className="goals-field">
            <label>Monthly Contribution</label>
            <input
              name="monthlyContribution"
              type="number"
              value={whatIf.monthlyContribution}
              onChange={handleWhatIfChange}
            />
          </div>
          <div className="goals-field">
            <label>Expected Return (%)</label>
            <input
              name="expectedAnnualReturn"
              type="number"
              value={whatIf.expectedAnnualReturn}
              onChange={handleWhatIfChange}
            />
          </div>
          <div className="goals-field">
            <label>Months</label>
            <input
              name="months"
              type="number"
              value={whatIf.months}
              onChange={handleWhatIfChange}
            />
          </div>
          <button type="submit" className="goals-submit">
            Run Simulation
          </button>
        </form>
        {whatIfResult && (
          <div className="whatif-result">
            {whatIfResult.trajectory && whatIfResult.trajectory.length > 0 && (
              <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px', marginBottom: '1rem' }}>
                <h3 style={{ marginBottom: '0.5rem' }}>Simulation Trajectory</h3>
                <svg viewBox="0 0 600 200" style={{ width: '100%', height: '200px' }}>
                  {(() => {
                    const trajectory = whatIfResult.trajectory || [];
                    if (trajectory.length === 0) return null;
                    
                    const values = trajectory.map(p => Number(p.value || 0));
                    const minValue = Math.min(...values, Number(whatIfResult.currentValue || 0));
                    const maxValue = Math.max(...values, Number(whatIfResult.currentValue || 0));
                    const range = maxValue - minValue || 1;
                    const padding = 40;
                    const width = 600 - (padding * 2);
                    const height = 200 - (padding * 2);
                    
                    const points = trajectory.map((point, index) => {
                      const x = padding + (index / Math.max(trajectory.length - 1, 1)) * width;
                      const y = padding + height - ((Number(point.value || 0) - minValue) / range) * height;
                      return { x, y, value: Number(point.value || 0), date: point.date };
                    });
                    
                    const pathData = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
                    
                    return (
                      <>
                        <line x1={padding} y1={padding} x2={padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                        <line x1={padding} y1={padding + height} x2={width + padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                        <path d={pathData} fill="none" stroke="#8b5cf6" strokeWidth="3" strokeLinecap="round" />
                        {points.map((point, index) => (
                          <circle key={index} cx={point.x} cy={point.y} r="3" fill="#8b5cf6" />
                        ))}
                        <text x={padding + width / 2} y={height + padding + 20} textAnchor="middle" fontSize="10" fill="var(--text-light)">Months</text>
                      </>
                    );
                  })()}
                </svg>
              </div>
            )}
            <p>{whatIfResult.narrative}</p>
            <div className="whatif-metrics">
              <span>Projected Value: {whatIfResult.projectedValue?.toFixed(2)}</span>
              <span>End Date: {whatIfResult.endDate}</span>
            </div>
          </div>
        )}
      </GlowCard>
    </div>
  );
};

export default GoalsForecastPage;
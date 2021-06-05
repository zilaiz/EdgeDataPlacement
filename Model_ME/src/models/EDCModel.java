package models;

import java.util.List;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloMultiCriterionExpr;
import ilog.concert.IloNumExpr;
import ilog.cp.IloCP;
import objectives.User;

public class EDCModel {

	private int mServersNumber;
	private int[][] mDistanceMatrics;
	private int[][] mUserBenefits;
	private List<User> mUsers;

	private double mBenefitPerReplica;
	private double mCost;
	private double mAllBenefits;

	public EDCModel(int serversNumber, int[][] distanceMatrics, int[][] userBenefits, List<User> users) {
		mServersNumber = serversNumber;
		mDistanceMatrics = distanceMatrics;
		mUserBenefits = userBenefits;
		mUsers = users;
	}

	public void runEDC() {
		try {

			IloCP cp = new IloCP();

			IloIntVar[] r = cp.intVarArray(mServersNumber, 0, 1);

			IloNumExpr[] exprs = new IloNumExpr[2];
			IloLinearNumExpr rExpr = cp.linearNumExpr();
			IloNumExpr dExpr = cp.linearNumExpr();

			// int[][] savedHops = new int[r.length][r.length];
			// IloNumExpr[][] benefitsExprs = new IloNumExpr[r.length][r.length];

			IloNumExpr[] maxBenifitsExprs = new IloNumExpr[mUsers.size()];

			// for (int i = 0; i < r.length; i++) {
			// for (int j = 0; j < r.length; j++) {
			// if (mDistanceMatrics[i][j] < 2)
			// savedHops[i][j] = 2 - mDistanceMatrics[i][j];
			// else
			// savedHops[i][j] = 0;
			// }
			// }

			IloNumExpr[][] userBenefitsExprs = new IloNumExpr[mUsers.size()][mServersNumber];

			// int[][] userBenefits = new int[mServersNumber][mUsers.size()];
			//
			// for(int i = 0; i < mServersNumber; i++) {
			// for (int j = 0; j < mUsers.size(); j++) {
			// if (mUsers.get(j).nearEdgeServers.contains(i)) userBenefits[i][j] = 2;
			// else if (isConnected(mUsers.get(j).nearEdgeServers, i)) userBenefits[i][j] =
			// 1;
			// else userBenefits[i][j] = 0;
			// }
			// }

			for (int i = 0; i < mServersNumber; i++) {
				rExpr.addTerm(1, r[i]);
				for (int j = 0; j < mUsers.size(); j++) {
					// exchange row and column, then just get the column value to find the max
					// benefit

					// benefits[j][i] = cp.prod(savedHops[i][j] * mUserNumberList[j], r[i]);

					userBenefitsExprs[j][i] = cp.prod(mUserBenefits[i][j], r[i]);
				}
			}

			for (int j = 0; j < mUsers.size(); j++) {
				maxBenifitsExprs[j] = cp.max(userBenefitsExprs[j]);
			}

			dExpr = cp.sum(maxBenifitsExprs);

			exprs[0] = rExpr;
			exprs[1] = cp.negative(dExpr);

			IloMultiCriterionExpr moExpr = cp.staticLex(exprs);

			cp.add(cp.minimize(moExpr));

			// for (int i = 0; i < r.length; i++) {
			// IloConstraint c = cp.ge(r[i], 2);
			// for (int j = 0; j < r.length; j++) {
			// this constrain means:
			// existing j, r[j] * d[i][j] <= limit where r[j] = 1
			// the constrain will fail without +1 to distance and limit if r[j] = 0
			// thus, the constrain is converted to: 1 <= r[j] * (d[i][j] + 1) <= limit + 1,
			// then r[j] = 0 will not affect result
			// c = cp.or(c, cp.and(cp.le(cp.prod(r[j], mDistanceMatrics[i][j] + 1), 2),
			// cp.ge(cp.prod(r[j], mDistanceMatrics[i][j] + 1), 1)));
			//
			// }
			// cp.add(c);
			// }

//			for (int i = 0; i < mUsers.size(); i++) {
//
//				IloConstraint u = cp.ge(r[0], 2);
//				
//				for (int j = 0; j < r.length; j++) {
//					u = cp.or(u, cp.ge(userBenefitsExprs[i][j], 1));
//				}
//				cp.add(u);
//			}
			
			IloConstraint u = cp.ge(r[0], 0);
			for (int i = 0; i < mUsers.size(); i++) {
				u = cp.and(u, cp.ge(maxBenifitsExprs[i], 1));
			}
			cp.add(u);

			cp.setOut(null);

			if (cp.solve()) {
				mCost = cp.getObjValues()[0];
				mAllBenefits = -cp.getObjValues()[1];

				mBenefitPerReplica = mAllBenefits / mCost;

//				System.out.println("Solution status = " + cp.getStatus());
//				System.out.println("Total Cost = " + mCost);
//				System.out.println("Benefit/Cost = " + mBenefitPerReplica);
//				System.out.println("Selected servers are:");
//				for (int i = 0; i < r.length; i++) {
//					if (cp.getValue(r[i]) == 1)
//						System.out.print(i + " ");
//				}
//				System.out.println();

			} else {
				System.out.println(" No solution found ");
			}

			cp.end();
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}

	public double getCost() {
		return mCost;
	}

	public double getBenefitPerReplica() {
		return mBenefitPerReplica;
	}

	public double getAllBenefits() {
		return mAllBenefits;
	}
}

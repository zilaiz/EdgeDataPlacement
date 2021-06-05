package models;

import java.util.List;
import java.util.ArrayList;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloMultiCriterionExpr;
import ilog.concert.IloNumExpr;
import ilog.cp.IloCP;
import objectives.User;

public class ECCplexModel {
    private int mServersNumber; //服务器数量
    private int[][] mDistanceMatrix; //距离矩阵
    private int[][] mAccessMatrix; //可访问矩阵
    private int mhops; //跳数
    private int mPacketsNeed; //需要的分块数
    private List<Integer> mSelectedServerList; //数据节点列表

	private double mCost;

	public ECCplexModel(int serversNumber, int[][] distanceMatrix, int hops, int requiredpackets) {
		mServersNumber = serversNumber; 
        mDistanceMatrix = distanceMatrix;
        mhops = hops;
        mPacketsNeed = requiredpackets;
        mAccessMatrix = new int[mServersNumber][mServersNumber];
        mSelectedServerList = new ArrayList<>();
    }
    
    public void ConvertDistoAcc () //根据hops，将能访问到的节点全部置1，否则置0;同时计算度
    {
        for(int i = 0; i < mServersNumber; ++i)
        {
            for(int j = 0; j < mServersNumber; ++j)
            {
                if (mDistanceMatrix[i][j] <= mhops) //当最短距离小于hops时，可访问节点数自增
                {
                    mAccessMatrix[i][j] = 1;
                }
                else mAccessMatrix[i][j] = 0;
            }
        }
    }

	public void runECCplex() {
		try {

			IloCP cp = new IloCP();

            IloIntVar[] c = cp.intVarArray(mServersNumber, 0, 1); //r表示edge server中是否存在data caching,元素类型为IloIntVar，Doamin为{0,1}
			IloIntExpr cExpr = cp.intExpr(); //目标函数cost

            
            cExpr = cp.sum(c); //cExpr = c[0] + c[1] + ... + c[mServerNumber - 1], |D|
            cExpr = cp.prod(cExpr, 100); //分子放大100倍，减小整数除法舍入影响
            cExpr = cp.div(cExpr,mPacketsNeed);// cExpr = |D|*100 / m

            cp.add(cp.minimize(cExpr));// minmize(cExpr)
            
            


            ConvertDistoAcc ();
            IloConstraint u = cp.ge(c[0], 0);
            IloNumExpr[][] access = new IloNumExpr[mServersNumber][mServersNumber];
            IloNumExpr[] accsum = new IloNumExpr[mServersNumber];
            for (int i = 0; i < mServersNumber; ++i)
            {
                for (int j = 0; j < mServersNumber; ++j)
                {
                    access[i][j] = cp.prod(mAccessMatrix[i][j],c[j]); //计算当前节点能访问到多少个数据节点
                }
                    accsum[i] = cp.sum(access[i]);
                    u = cp.and(u,cp.ge(accsum[i],mPacketsNeed));  //保证访问到的数据节点大于m
            }

			cp.add(u);

			cp.setOut(null);

			if (cp.solve()) {
                for(int i = 0; i < mServersNumber; ++i)
                {
                    if(cp.getValue(c[i]) == 1.0)
                    {
                        mSelectedServerList.add(i);  //将c转化成节点列表
                    }
                }
                mCost = (double) mSelectedServerList.size() / (double) mPacketsNeed;


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
    public List<Integer> getSelectedServers()
    {
        return mSelectedServerList;
    }
    public int getPacketsNeed()
    {
        return mPacketsNeed;
    }
}

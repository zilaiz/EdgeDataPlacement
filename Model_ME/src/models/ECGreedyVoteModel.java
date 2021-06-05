package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Double;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ECGreedyVoteModel {
    private int mServersNumber;   //服务器数量
    private int[][] mAccessMatrix;   //可访问矩阵,初始为全0矩阵
    private int[][] mDistanceMatrix;  //最短距离矩阵
    private int mhops;     //允许的跳数
    private int mPacketsNeed;  //最后需要的分块数

    private double mCost; //Cost
    private double mReplicaCost;
    private Map<Integer,Integer> mDegrees; //度
    private Map<Integer,Integer> mDataPacketsNeed; //尚需数据块的个数
    private Map<Integer,Double> mVotes; //票数
    private Map<Integer,Double> mWeights; //票权
    private List<Integer> mSelectedServerList; //数据节点列表
    private List<Integer> mSelectedReplicaServerList;

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {  
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(byteOut);  
        out.writeObject(src);  
  
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
        ObjectInputStream in = new ObjectInputStream(byteIn);  
        @SuppressWarnings("unchecked")  
        List<T> dest = (List<T>) in.readObject();  
        return dest;  
    } 

    public ECGreedyVoteModel(int serversNumber, int [][]distancematrix, int hops) //构造函数
    {
        mServersNumber = serversNumber;
        mDistanceMatrix = distancematrix;
        mhops = hops;

        mAccessMatrix = new int[mServersNumber][mServersNumber];
        mSelectedServerList = new ArrayList<>();
        mDegrees = new HashMap<>();
        mDataPacketsNeed = new HashMap<>();
        mWeights = new HashMap<>();
        mVotes = new HashMap<>();
    }


    public void initDataPacketsNeed(int requiredpackets) 
    {
        for (int key = 0; key < mServersNumber; ++key) //将mDataPacketsNeed重置
        {
            mDataPacketsNeed.put(key,requiredpackets);
        }
    }

    
    public void ConvertDistoAccAndCalDegree () //根据hops，将能访问到的节点全部置1，否则置0;同时计算度
    {
        for(int i = 0; i < mServersNumber; ++i)
        {
            int access = 0; //可访问节点数
            for(int j = 0; j < mServersNumber; ++j)
            {
                if (mDistanceMatrix[i][j] <= mhops) //当最短距离小于hops时，可访问节点数自增
                {
                    access ++;
                    mAccessMatrix[i][j] = 1;
                }
                else mAccessMatrix[i][j] = 0;
            }
            mDegrees.put(i,access); //access为广义上的度
        }
    }


    public void makeVotes(List<Integer> selserverlist) //进行投票
    {
        if(!mVotes.isEmpty()){  //每次投票前，都要清空mVotes
            mVotes.clear();
        }
        for (int j = 0; j < mServersNumber; ++j)
        {
            double cur_vote = 0.0;
            if(!selserverlist.contains(j)) //将已经在mSelectedServerList中的节点进行排除
            {
                for(int i = 0; i < mServersNumber; ++i)
                {
                    cur_vote += mAccessMatrix[i][j] * mWeights.get(i); //票数 = 票权 * 是否可访问(0/1)
                }
                mVotes.put(j,cur_vote);
            }
        }
    }

    public static int getMapMaxValueKey(Map<Integer, Double> map)  //取出最大value对应的Key值,Int
    {
        List<Entry<Integer,Double>> list = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue().intValue() - o2.getValue().intValue()));
        int key = list.get(list.size() - 1).getKey();
        return key;
    }

    public int getMapMinValue(Map <Integer,Integer> maps)  //取出最小value
    {
        Comparator<Map.Entry<Integer, Integer>> valCmp = new Comparator<Map.Entry<Integer,Integer>>() {
            @Override
            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                return o1.getValue().intValue()-o2.getValue().intValue();  // 升序排序
            }
        };
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(maps.entrySet()); //传入maps实体
        Collections.sort(list,valCmp);

        return list.get(0).getValue();
    }


    public int selectServerwithHighestVotes() //选择有最高票数的节点
    {
        return getMapMaxValueKey(mVotes);
    }


    public boolean checkPacketsRequired() //检查是否所有点都已满足，若是返回true
    {
        int count = 0;
        for (int val:mDataPacketsNeed.values())
        {
            if (val == 0){
                count++;
            }
        }
        if (count == mDataPacketsNeed.size())
        {
            return true;
        }
        return false;
    }



    public void runECGreedyVoteCost() throws ClassNotFoundException, IOException
    { // 生成放置方案
        ConvertDistoAccAndCalDegree(); //首先进行矩阵转化，度计算
        double min_cost = Double.MAX_VALUE;
        int best_pn = 2; //最低cost对应的数据块数
        List<Integer> MinCostServerList = new ArrayList<>();
        int mindeg = getMapMinValue(mDegrees);
        for (int m = 1; m <= mindeg; ++m)
        {
            initDataPacketsNeed(m); //初始化mDataPacketsNeed
            double cost = 0.0;
            List<Integer> SelectedServerList = new ArrayList<>();
            while(!checkPacketsRequired()) //判断mDataPacketsNeed是否全为0或者mVotes是否为空，若是则结束循环，否则继续
            {
                for(int key = 0; key < mServersNumber; ++key)
                {
                    mWeights.put(key,(double)mDataPacketsNeed.get(key)); //计算票权，为当前节点还需的数据块数
                }
                makeVotes(SelectedServerList); //进行投票
                int VoteHighestServer = selectServerwithHighestVotes(); //VoteHighest为票数最高的服务器
                SelectedServerList.add(VoteHighestServer);

                for (int c = 0; c < mServersNumber; ++c)
                {  //对VoteHighestServer可访问节点的mDataPacketsNeed值减1
                    if(mAccessMatrix[VoteHighestServer][c] == 1)
                    {
                        int v = mDataPacketsNeed.get(c);
                        if(v > 0){
                            mDataPacketsNeed.put(c,v-1); 
                        }
                    }
                }
            }
            cost = (double)SelectedServerList.size()/(double)m;
            if(m == 1)
            {
                mReplicaCost = cost;
                List<Integer> intermedia_replica = deepCopy(SelectedServerList);
                mSelectedReplicaServerList = intermedia_replica;
                // System.out.println("Repilca:" + SelectedServerList + " cost:" + cost);
            }
            // if(m >= 2)
            // {
            //     System.out.println(m + ":" + SelectedServerList + " cost:" + cost);
            // }
            if(m >= 2 && cost < min_cost){
                min_cost = cost;
                List<Integer> intermedia = deepCopy(SelectedServerList);
                MinCostServerList = intermedia;
                best_pn = m;
            }
        }
        // System.out.println("\nBestOfGreedyVote:" + best_pn + ":" + MinCostServerList + " cost:" + min_cost);
        mCost = min_cost;
        mSelectedServerList = MinCostServerList;
        mPacketsNeed = best_pn;
    }



    public double getCost()
    {
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

    public double getReplica()
    {
        return mReplicaCost;
    }

    public List<Integer> getSelectedReplicaServers()
    {
        return mSelectedReplicaServerList;
    }
    
}

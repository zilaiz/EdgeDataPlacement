# %%
import networkx as nx
import matplotlib.pyplot as plt
import numpy as np

servernumber = 10
# %%
points = list(range(servernumber))
selpoints = [5, 7, 8, 9]
rmpoints = []
for i in points:
    if i not in selpoints:
        rmpoints.append(i)

#%%
G = nx.Graph()

G.add_nodes_from(selpoints)
G.add_nodes_from(rmpoints)

Matrix = np.array([
[0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
[0, 0, 0, 0, 0, 1, 0, 0, 0, 0],
[0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
[0, 0, 0, 0, 0, 0, 0, 1, 0, 0],
[0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
[0, 1, 0, 0, 0, 0, 0, 1, 0, 1],
[0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
[0, 0, 0, 1, 0, 1, 0, 0, 1, 0],
[1, 0, 0, 0, 1, 0, 0, 1, 0, 1],
[0, 0, 1, 0, 0, 1, 1, 0, 1, 0],
]
)

for i in range(servernumber):
    for j in range(servernumber):
        if(Matrix[i][j] > 0):
            G.add_edge(i,j)

position = nx.circular_layout(G)
nx.draw_networkx_nodes(G,position, nodelist=selpoints, node_color="r")
nx.draw_networkx_nodes(G,position, nodelist=rmpoints, node_color="b")
nx.draw_networkx_edges(G,position)
nx.draw_networkx_labels(G,position)      
plt.show()
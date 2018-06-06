from mpl_toolkits.mplot3d import Axes3D 
from matplotlib.ticker import ScalarFormatter
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


def read_solutions(file):
    data = pd.read_csv(file, delimiter=',',header=None)
    # return data.values.tolist()
    return data



solutions = read_solutions('CombinedParetoR2.csv')
y = solutions[0]
x = solutions[1]

plt.scatter(x, y, color='blue',label='NSGA-II')
plt.ylabel('Objective Function 1')
plt.xlabel('Objective Function 2')
plt.legend(loc='upper right')
plt.show()

solutions = read_solutions('CombinedParetoR3.csv')
z = solutions[2]
y = solutions[1]
x = solutions[0]

fig = plt.figure()
ax = fig.gca(projection='3d')

ax.scatter(x, y, z, marker='o',color='blue',label='NSGA-II')
ax.set_xlabel('Objective Function 1')
# ax.get_xaxis().get_major_formatter().set_useOffset(ScalarFormatter(useMathText=True))

ax.set_ylabel('Objective Function 2')
ax.set_zlabel('Objective Function 3')

# -------------------------------------------------------------------------------------------



plt.legend(loc='upper right')

plt.show()
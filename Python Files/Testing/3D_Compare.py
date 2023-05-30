import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

s = 'All_Values.csv'
t = pd.read_csv(str(s))

fig = plt.figure()
ax = fig.add_subplot(projection='3d')
fig.suptitle('Testing', fontsize=20)
plt.xlabel('Alpha', fontsize=18)
plt.ylabel('Cumulative Dose', fontsize=16)
plt.xticks(fontsize=15)
plt.yticks(fontsize=15)

lam = t.iloc[:, 0]
alpha = t.iloc[:, 1]
delta = t.iloc[:, 2]
psi = t.iloc[:, 3]
cumul = t.iloc[:, 4]
frac_size = t.iloc[:, 5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

ax.scatter3D(alpha, cumul, psi, s=1)

plt.show()

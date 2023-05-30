import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style
from mpl_toolkits.axes_grid1.inset_locator import inset_axes

style.use('default')

s3 = 'All_Values.csv'
t3 = pd.read_csv(s3, header=0)
t3 = t3.apply(pd.to_numeric, errors='coerce')
fig, ax = plt.subplots()

lam = t3.iloc[:, 0]
alpha = t3.iloc[:, 1]
delta = t3.iloc[:, 2]
psi = t3.iloc[:, 3]
cumul = t3.iloc[:, 4]
frac_size = t3.iloc[:, 5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

# one, two, three = np.array_split(t3, 3)
# ax.scatter(one.iloc[:, 5], one.iloc[:, 2], c='r', s=10, label='Linear')
# ax.scatter(two.iloc[:, 5], two.iloc[:, 2], c='g', s=10, label='Logistic')
# ax.scatter(three.iloc[:, 5], three.iloc[:, 2], c='b', s=10,
#            label='Linear-Quadratic')

ax.scatter(frac_size, gamma, c='k', s=10)

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(3)
for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=3)

ax.set_xlabel('Fraction Size (Gy)', fontsize=20, labelpad=10)
ax.set_ylabel(r'$\gamma$', fontsize=20, labelpad=10)
ax.tick_params(axis='both', which='major', labelsize=20)

ax.set_xticks(np.linspace(0, 30, 3))
ax.set_yticks(np.linspace(0, 1, 3))

# plt.legend(loc='lower right', fontsize=12)
# subax1 = inset_axes(ax, width=1.3, height=0.9, loc='upper left')
# subax1.set_xticklabels('')
# subax1.set_yticklabels('')
# subax1.tick_params(width=0)

# m = 2
# s = 8
# one, two, three = one.where(one.iloc[:, 5] < m),
#                             two.where(two.iloc[:, 5] < m),
#                             three.where(three.iloc[:, 5] < m)
# subax1.scatter(one.iloc[:, 5], one.iloc[:, 2], c='r', s=s)
# subax1.scatter(two.iloc[:, 5], two.iloc[:, 2], c='g', s=s)
# subax1.scatter(three.iloc[:, 5], three.iloc[:, 2], c='b', s=s)

# ax.indicate_inset_zoom(subax1, edgecolor="black", linewidth=5,
#                        alpha=0.75, visible=True)
plt.tight_layout()
plt.show()

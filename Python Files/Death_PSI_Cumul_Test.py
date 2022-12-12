import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors

style.use('default')
fig, ax = plt.subplots()

s = 'Figure Data/Alpha_PSI_dose.csv'
s2 = 'Figure Data/Delta_PSI_dose.csv'
t = pd.read_csv(s)
t2 = pd.read_csv(s2)

alpha = t.iloc[:, 1]
delta = t2.iloc[:, 2]
psi_direct = t.iloc[:, 3]
psi_indirect = t2.iloc[:, 3]
cumul_direct = t.iloc[:, 4]
cumul_indirect = t2.iloc[:, 4]
frac_size_direct = t.iloc[:, 5]
frac_size_indirect = t2.iloc[:, 5]
num_direct = cumul_direct / frac_size_direct
num_indirect = cumul_indirect / frac_size_indirect
# gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

# ax.set_xlabel(r'$\alpha$' + ' (' + r'Gy$\mathregular{^{-1}}$' + ')',
#               fontsize=20, labelpad=10)
ax.set_xlabel(r'$\delta$', fontsize=20, labelpad=10)
ax.set_ylabel('PSI', fontsize=20, labelpad=10)
ax.tick_params(axis='both', which='major', labelsize=20)
ax.tick_params(axis='both', which='both', pad=10)
# ax.set_xlim((0.06, 0.14))
ax.set_xlim((0.01, 0.09))
ax.set_ylim((0.6, 1))
xmin, xmax = ax.get_xlim()
ymin, ymax = ax.get_ylim()
# ax.set_aspect(5 / 120)
ax.axvspan(xmin, xmax, facecolor='white', alpha=0.15)
ax.set_xticks(np.linspace(xmin, xmax, 3))
ax.set_yticks(np.linspace(ymin, ymax, 3))

cmap = cm.get_cmap('viridis_r')
norm = colors.Normalize(min(cumul_direct.min(), cumul_indirect.min()), max(
    cumul_direct.max(), cumul_indirect.max()))

# x = alpha
x = delta
y = psi_indirect
c = cumul_indirect
sc = plt.scatter(x, y, c=c, cmap=cmap, norm=norm, s=10)

for axis in ['bottom', 'left']:
    ax.spines[axis].set_linewidth(3)

for axis in ['top', 'right']:
    ax.spines[axis].set_linewidth(0)
ax.tick_params(width=2)

cb = plt.colorbar(sc)
cb.ax.set_ylabel('Cumulative Dose (Gy)', labelpad=20, fontsize=20)
cb.ax.tick_params(axis='both', which='major', labelsize=15)

# x = [0.08, 0.08, 0.12, 0.12]
# y = [0.9, 0.7, 0.7, 0.9]
x = [0.03, 0.07, 0.03, 0.07]
y = [0.9, 0.9, 0.7, 0.7]
plt.scatter(x, y, marker='x', c='black', s=200, linewidth=5)

fig.set_size_inches(6.8, 5)
plt.tight_layout()
plt.show()

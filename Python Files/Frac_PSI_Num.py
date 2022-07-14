import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm, colors
from textwrap import wrap

style.use('ggplot')

threshold = 0.322

fig34, ax = plt.subplots()
m = 6
min_val, max_val = 0,1
n = 20
orig_cmap = cm.viridis_r
col = orig_cmap(np.linspace(min_val, max_val, m))
cmap = colors.LinearSegmentedColormap.from_list("mycmap", col, N = m)

s3 = '../SPARK Project/All_Values.csv'
t3 = pd.read_csv(s3)
cumul = t3.iloc[:,4]
frac_size = t3.iloc[:,5]
num = cumul / frac_size
t3 = t3[num < m]

lam = t3.iloc[:,0]
alpha = t3.iloc[:,1]
delta = t3.iloc[:,2]
psi = t3.iloc[:,3]
k = 100.0 / psi;
cumul = t3.iloc[:,4]
frac_size = t3.iloc[:,5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

x = frac_size
y = psi
c = num
sc = plt.scatter(x, y, c= c, cmap = cmap, edgecolors='none')

correlation_xy = np.corrcoef(x, y)[0,1]
print(correlation_xy**2)

title = plt.title("\n".join(wrap("Effect of Changing Fraction Size (Gy) on PSI", 60)), 
          fontsize = 20)
title.set_y(5)
plt.xlabel("Fraction Size (Gy)", fontsize = 20, labelpad = 20)
plt.ylabel("Proliferation Saturation Index", fontsize = 20, labelpad = 20)
ax.tick_params(axis = 'both', which = 'major', labelsize = 15)

step = 1
norm = colors.Normalize(vmin= 0, vmax= step * m)
sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)
labels = np.linspace(0, step * m, m + 1)
loc = labels - step / 2
cb = plt.colorbar(sm, ax=ax, format = '%.2f')
cb.set_ticks(loc)
tick = cb.set_ticklabels(labels)


cb.ax.set_ylabel('Number of Fractions', labelpad = 30, fontsize = 20)

cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 15)

#labels = np.arange(0, m, 1)
#loc = labels + (1 / 2)
#cb.set_ticks(loc)
#cb.ax.set_yticklabels([i+1 for i in labels])
#ax.set_aspect(60)
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.patches as mpatches
#from matplotlib import cm
from matplotlib import style, cm

style.use('default')

#psi = 0.9
threshold = 0.322

s = 'Volume_Data.csv'
t = pd.read_csv(s)
#t2 = pd.read_csv(s2)
s3 = '../SPARK Project/All_Values.csv'
t3 = pd.read_csv(s3)
fig, ax = plt.subplots()

lam = t3.iloc[:,0]
alpha = t3.iloc[:,1]
delta = t3.iloc[:,2]
psi = t3.iloc[:,3]
#k = 100.0 / psi;
cumul = t3.iloc[:,4]
frac_size = t3.iloc[:,5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

#cmap = cm.get_cmap('winter_r')
#colors = cmap(np.linspace(0,1,len(psi)))
#x = t.iloc[:,0]
x = (t.iloc[:,0] - t.iloc[:,0][1])/ 7 
#x = (t.iloc[:,0] - 1)/ 7
#x = ((t.iloc[:,0] - 1)/ 7).iloc[1:]
#t.iloc[:, 1:] = (t.iloc[:,1:] - 1)/ 7
numpts = 0

#condition = t3[(psi > 0.97) & (delta < 0.02)]
#reduced = list(condition.index.values + 1)
#t = t.iloc[:,reduced]

for i in range(1, len(t.columns)):
    if t.iloc[:,i][len(t.iloc[:,i].dropna()) - 1] < (1 - threshold) * t.iloc[:,i][1]:
        c = 'g'
        numpts += 1
    else:
        c = 'r'
    t.iloc[:,i] = t.iloc[:,i] * 100 / t.iloc[:, i][0]
    #c = 'g' if max(t.iloc[:,i]) < k[i - 1] else 'r'
    #l = 4 if t.iloc[:,i][t.iloc[:,i].count() - 1] < (1 - threshold) * t.iloc[:,i][1] else 5
    #plt.plot(x, t.iloc[:,i], linewidth = 5, c = colors[i - 1], label = t.columns[i])
    #plt.plot(x, t.iloc[:,i].iloc[1:], c = 'dodgerblue')
    plt.scatter(x, t.iloc[:,i], c = c, zorder=2)
    plt.plot(x, t.iloc[:,i], linewidth = 0.15, c = c, zorder=2)

#plt.vlines(4, 0, max(t.iloc[:,1:].loc[4 * 7 * 24]), linestyles = 'dotted', color = 'black', linewidth = 5, zorder=3)
#plt.hlines((1 - threshold) * 100,t.iloc[:,0][0],(t.iloc[:,0][len(t.iloc[:,1]) - 1] - 1)/ 7, colors = 'k', linestyles = 'dotted', color = 'black', linewidth = 5, zorder=3)
#green_patch = mpatches.Patch(color='g', label = 'Locoregional Control')
#red_patch = mpatches.Patch(color='r', label = 'Locoregional Failure')

#ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
#plt.xticks(range(8))
plt.xlabel('Weeks Since Start of RT')
plt.ylabel('Normalized Tumor Volume')
plt.xlim((min(x) - 1/7, max(x) + 1/7))
plt.ylim((0))
ymin, ymax = ax.get_ylim()
xmin, xmax = ax.get_xlim()
ax.axvspan(xmin + 1/7, xmax - 1/7, facecolor='lightgray', alpha=0.5)

for time in np.arange(xmin, xmax, 1/7 * 1/24):
    weekend = (int) (time * 24 * 7) % (24 * 7)
    if weekend <= 120:
        ax.axvspan(time, time + 1/24, facecolor='white', alpha=0.5)
#plt.ylabel('Normalized Tumor Volume', fontsize = 25, fontweight = 2, labelpad = 12)
#plt.legend(handles=[green_patch, red_patch], fontsize = 20, loc='upper right')
#ax.set_aspect(1/20)
#plt.legend(fontsize = 12)
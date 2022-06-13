import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import style
import matplotlib.gridspec as gridspec

style.use('default')

outer_grid = gridspec.GridSpec(1,3, width_ratios=(2,2,2))
fig = plt.figure(figsize = (15,5))
axs = [plt.subplot(outer_grid[:,0]), plt.subplot(outer_grid[:,1]), plt.subplot(outer_grid[:,2])]

start_psi = 0.8
start_psi = 1
threshold = 0.322
x = 1
axis_thickness = 3;

directory = '../SPARK Project/Paper Edited Figures/Figure 1/'

a1 = directory + 'Logistic_Volume.csv'
b1 = directory + 'Logistic_k.csv'
c1 = directory + 'Logistic All_Values.csv'
a2 = directory + 'Direct_Volume.csv'
b2 = directory + 'Direct_k.csv'
c2 = directory + 'Direct All_Values.csv'
a3 = directory + 'Indirect_Volume.csv'
b3 = directory + 'Indirect_k.csv'
c3 = directory + 'Indirect All_Values.csv'

volume = [pd.read_csv(a1), pd.read_csv(a2), pd.read_csv(a3)]
k_vals = [pd.read_csv(b1), pd.read_csv(b2), pd.read_csv(b3)]
all_values = [pd.read_csv(c1), pd.read_csv(c2), pd.read_csv(c3)]
letter = ['A', 'B', 'C']

for i in range(3):
    ax = axs[i]
    logistic = False
    direct = False
    indirect = False
    
    t = volume[i]
    t2 = k_vals[i]
    t3 = all_values[i]
    
    lam = t3.iloc[:,0]
    alpha = t3.iloc[:,1]
    delta = t3.iloc[:,2]
    psi = t3.iloc[:,3]

    if i == 0:
        logistic = True
    if i == 1:
        direct = True
    if i == 2:
        indirect = True
        
    ax.plot((t.iloc[:,0]) / 7, t.iloc[:,1], label = r'$V$', c= 'dodgerblue', linewidth = 3)
    
    ax.plot((t2.iloc[:, 0]) / 7, t2.iloc[:, 1], label = r'$K$', c= 'darkorange', linewidth = 3)
    
    ax.set_xlim((0, 4))
    ax.set_ylim((0, (100 / start_psi) + 5))
    ymin, ymax = ax.get_ylim()
    xmin, xmax = ax.get_xlim()
    #ax.set_aspect(xmax / ymax)
    
    #ax.text(-0.35, 1.2, letter[i], transform=ax.transAxes,
      #color='black', fontweight = 'bold', fontsize = 30, va='top')
    
    props = dict(boxstyle='square', facecolor='none', edgecolor='none', pad = 0.2) #lightgray
    props_blank = dict(boxstyle='square', facecolor='none', edgecolor='none', pad = 0.2)
    props2 = dict(boxstyle='square', facecolor = 'none', edgecolor='none', pad = 0.2) #lightgray
        
    #ax.axvspan(xmin, xmax, facecolor='lightgray', alpha=0.5)
    
    # for time in np.arange(xmin, xmax, 1/7 * 1/24):
    #     weekend = (int) (time * 24 * 7) % (24 * 7)
    #     if weekend < 120:
    #         ax.axvspan(time, time + 1/24, facecolor='white', alpha=0.5)
    
    ax.set_xticks(np.arange(0, xmax + 1, 1))
    ax.set_yticks(np.linspace(0, 100, 3))
    
    if i == 0:
        title = 'Sample Logistic Growth'
        x_label = 'Time'
        y_label = 'Tumor Volume'
        ax.legend(loc = 'lower right', fontsize = 20)
        ax.set_xticks([])
        ax.set_yticks([])
    else:
        x_val = [x/7 for x in range(0, 28) if x % 7 < 5]
        y_val = [105] * len(x_val)
        ax.scatter(x_val, y_val, marker=r'$\downarrow$', c = 'green', s = 300, linewidth = 0.25, edgecolors='black', zorder = 5)    
    
        title = 'Sample ' + ('Indirect' if indirect else 'Direct') + ' Cell Kill'
        x_label = 'Time on RT (week)'
        y_label = '% Initial Tumor Volume'

        ax.tick_params(axis = 'both', which = 'major', labelsize = 20)
        ax.tick_params(axis = 'both', which = 'both', pad = 10, width = axis_thickness)
        
        alpha_text = r'$\alpha=%.2f$' % (alpha[x - 1]) +  ' ' + '$Gy^{-1}$' if direct else ''
        delta_text = r'$\delta=%.2f$' + str(delta[x - 1]) if indirect else ''
        lambda_text = '\n' + r'$\lambda=%.2f$' % (lam[x - 1]) + ' ' + '$day^{-1}$' + '\n'
        psi_text = r'$\mathrm{PSI}=%.2f$' % (psi[x - 1])
        
        textstr_1 = ''.join((alpha_text, delta_text, lambda_text, psi_text))
        textstr_2 = ''.join((r'$\delta=%.2f$' + str(delta[x - 1]), lambda_text, psi_text))  
        ax.text(0.05, 0.30 if direct else 0.28, textstr_1, fontsize = 15, transform=ax.transAxes, 
                     verticalalignment='top', bbox=props if direct else props_blank, zorder = 4)     
        if indirect:
            ax.text(0.05, 0.30, textstr_2, color = 'white', fontsize = 15, transform=ax.transAxes, 
            verticalalignment='top', bbox=props2, zorder = 3)            

    ax.set_title(title, fontsize = 20, pad = 15)
    ax.set_xlabel(x_label, fontsize = 20, labelpad = 10)
    ax.set_ylabel(y_label, fontsize = 20, labelpad = 10)
    for axis in ['bottom','left']:
      ax.spines[axis].set_linewidth(axis_thickness)
    for axis in ['top','right']:
      ax.spines[axis].set_linewidth(0)

plt.tight_layout()


outer_grid = gridspec.GridSpec(1,3, width_ratios=(2,2,2))
fig2 = plt.figure(figsize = (15,5))
axs = [plt.subplot(outer_grid[:,0]), plt.subplot(outer_grid[:,1]), plt.subplot(outer_grid[:,2])]
for i in range(3):
    ax = axs[i]
    ax.text(-0.35, 1.2, letter[i], transform=ax.transAxes,
      color='black', fontweight = 'bold', fontsize = 30, va='top')
plt.tight_layout()

plt.show()
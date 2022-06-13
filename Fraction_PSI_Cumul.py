import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import style, cm

style.use('ggplot')

s3 = '../SPARK Project/All_Values.csv'
t3 = pd.read_csv(s3)

lam = t3.iloc[:,0]
alpha = t3.iloc[:,1]
delta = t3.iloc[:,2]
psi = t3.iloc[:,3]
k = 100.0 / psi;
cumul = t3.iloc[:,4]
frac_size = t3.iloc[:,5]
num = cumul / frac_size
gamma = 1 - np.exp(-alpha * frac_size - (alpha / 10) * pow(frac_size, 2))

fig3, ax = plt.subplots()
cmap = cm.get_cmap('viridis_r')
x = frac_size
y = psi
c = cumul
sc = plt.scatter(x,y,c=c,cmap = cmap)

# c = [0 for i in range(int(len(frac_size) / 2))] + [1 for j in range(int(len(frac_size) / 2))]
# sc = plt.scatter(x, y, c= c)

# x1, x2 = np.array_split(x, 2)
# y1, y2 = np.array_split(y, 2)
# c1, c2 = np.array_split(c, 2)
# comm = np.intersect1d(x1, x2)

correlation_xy = np.corrcoef(x, y)[0,1]
print(correlation_xy**2)

plt.title('Fraction Size vs. Initial Proliferation Saturation to Reach 32.2% Threshold Following RT for ' + r'$\alpha$' + " = " + str(alpha[0]), fontsize = 20)
plt.xlabel('Fraction Size (Gy)', fontsize = 20, labelpad = 20)
plt.ylabel('PSI (Proliferation Saturation)', fontsize = 20, labelpad = 20)
ax.tick_params(axis = 'both', which = 'major', labelsize = 15)
#ax.set_aspect(50)

#norm = colors.Normalize(vmin= 0, vmax= (max(cumul)))
#sm = plt.cm.ScalarMappable(norm = norm, cmap = cmap)
cb = plt.colorbar(sc)
cb.ax.set_ylabel('Cumulative Dose (Gy)', labelpad = 30, fontsize = 20)
cb.ax.tick_params(axis = 'both', which = 'major', labelsize = 15)
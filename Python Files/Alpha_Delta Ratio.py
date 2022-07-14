import numpy as np
import matplotlib.pyplot as plt
from sklearn import svm
import pandas as pd

psi = 0.25
num_pts = 10000
param = pd.read_csv("Grid Search Filtered psi_" + str(psi) + " " + str(num_pts) + "_pts.csv")

fig = plt.figure()
ax = fig.add_subplot(projection="3d")
lam, alpha, delta, psi_check = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3]
#lam, alpha, delta, psi_check = map(pd.Series, zip(*sorted(zip(delta, lam, alpha, psi_check), reverse=True)))
adratio = pd.Series([alpha[i]/delta[i] for i in range(len(psi_check))])

m = 2.
d = np.abs(adratio - np.median(adratio))
mdev = np.median(d)
s = d / (mdev if mdev else 1.)

#lam = lam[psi_check == 0]
#alpha = alpha[psi_check == 0]
#delta = delta[psi_check == 0]
#psi_check = psi_check[psi_check == 0]

X = np.hstack((lam.values[:, None], alpha.values[:, None], delta.values[:, None]))
Y = psi_check.to_numpy()

#adratio = adratio[s < m]
#X = X[s < m]
#Y = Y[s < m]

#svc = svm.LinearSVC(C = 1e10, tol = 1e-8, dual = False, penalty = 'l1', max_iter = 10000)
#svc.fit(X,Y)

#z = lambda x,y: (-svc.intercept_[0]-svc.coef_[0][0]*x-svc.coef_[0][1]*y) / svc.coef_[0][2]

m = 1.05
xlim = np.linspace(lam.min() * m, lam.max() * m,8)
ylim = np.linspace(alpha.min() * m, alpha.max() * m,8)
x,y = np.meshgrid(xlim,ylim)

#ax.plot_surface(x, y, z(x,y), color = 'k', linewidth = 0, antialiased = False)
#ax.plot_surface(x, y, y, color = 'k', linewidth = 0, antialiased = False)

#c = ['lime' if adratio[i] <= 1 else ('lightcoral') for i in range(len(adratio))]
cmap = plt.get_cmap('cool')
#s = [5 if adratio[i] != -1 else 0 for i in range(len(adratio))]
sc = ax.scatter3D(X[Y==0,0], X[Y==0,1], X[Y==0,2], c = adratio, s = 5, cmap = cmap)
#ax.scatter3D(X[Y==1,0], X[Y==1,1], X[Y==1,2], c = 'b', s = 0.5)

#sc = ax.scatter(X[:, 0], X[:, 1], X[:, 2], c=Y, cmap=cmap, s = 0.1)
ax.set_xlabel(r'$\lambda$' + ": Lambda", fontsize = 20, labelpad = 20)
ax.set_xlim(0, max(lam[psi_check == 0]))
ax.set_ylabel(r'$\alpha$' + ": Alpha", fontsize = 20, labelpad = 20)
ax.set_ylim(0, max(alpha[psi_check == 0]))
ax.set_zlabel(r'$\delta$' + ": Delta", fontsize = 20, labelpad = 20)
ax.set_zlim(0, max(delta[psi_check == 0]))
ax.tick_params(axis='both', which='major', pad = 5)

#red_patch = mpatches.Patch(color='red', label='V(i) / K(i) > 1')
#blue_patch = mpatches.Patch(color='blue', label='V(i) / K(i) <= 1')
cbr = plt.colorbar(sc)

#plt.legend(handles=[red_patch, blue_patch], fontsize = 20, loc='upper right')
plt.title(r'$\alpha$' + "/" + r'$\delta$' + " Ratio for " + str(len(X[:,0])) + " Patients for PSI = " + str(psi), fontsize = 30, fontweight = 2)
plt.tight_layout()

fig = plt.figure()
ax = fig.add_subplot(111)
plt.boxplot(adratio)
plt.title("Box Plot of " + r'$\alpha$' + "/" + r'$\delta$' + " Ratio, PSI = " + str(psi))
for tick in ax.xaxis.get_major_ticks():
    tick.tick1line.set_visible(False)
    tick.label1.set_visible(False)
    
#fig.savefig("Boxplot of " + str(num_pts) + ", PSI = " + str(psi) + str(".png"))
#plt.tight_layout()
plt.show()
#z = lambda x,y: (-svc.intercept_[0]-svc.coef_[0][0]*x-svc.coef_[0][1]*y) / svc.coef_[0][2]
#print(str(svc.coef_[0][0]) + "," + str(svc.coef_[0][1]) + "," + str(svc.coef_[0][2]) + "," + str(svc.intercept_[0]))
#print("a = " + str(svc.coef_[0][0]) + "; b = " + str(svc.coef_[0][1]) + "; c = " + str(svc.coef_[0][2]) + "; d = " + str(svc.intercept_[0]) + ";")

#print(str(svc.coef_[0][0]) + "x + ", end = '')
#print(str(svc.coef_[0][1]) + "y + " , end = '')
#print(str(svc.coef_[0][2]) + "z + ", end = '') 
#print(str(svc.intercept_[0]) + " = 0")
#print(svc.n_iter_)
#print("Linear: " + str(svc.score(X,Y)))
#print(len(adratio[adratio <= 1]))

#svc = svm.SVC()
#svc.fit(X,Y)
#print("RBF: " + str(svc.score(X,Y)))
max = X[adratio == adratio.max()]

median = X[adratio == adratio.quantile(interpolation='nearest')]

min = X[adratio == adratio.max()]

print(max, median, min)
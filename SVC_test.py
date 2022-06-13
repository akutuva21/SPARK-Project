import numpy as np
import matplotlib.pyplot as plt
from sklearn import svm
import pandas as pd
import matplotlib.patches as mpatches

param = pd.read_csv("Grid Search psi_0.9 100000_pts.csv")

fig = plt.figure()
ax = fig.add_subplot(projection="3d")
lam, alpha, delta, psi_check = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3]
#lam, alpha, delta, psi_check = map(pd.Series, zip(*sorted(zip(delta, lam, alpha, psi_check), reverse=True)))
#a_d_ratio = pd.Series([1 if alpha[i] > delta[i] and psi_check[i] == 0 else (0) for i in range(len(psi_check[psi_check == 0]))])

X = np.hstack((lam.values[:, None], alpha.values[:, None], delta.values[:, None]))
Y = psi_check.to_numpy()

svc = svm.LinearSVC(C = 1e10, tol = 1e-8, dual = False, penalty = 'l1', max_iter = 10000)
svc.fit(X,Y)

z = lambda x,y: (-svc.intercept_[0]-svc.coef_[0][0]*x-svc.coef_[0][1]*y) / svc.coef_[0][2]

s = 1.05
xlim = np.linspace(lam.min() * s, lam.max() * s,8)
ylim = np.linspace(alpha.min() * s, alpha.max() * s,8)
x,y = np.meshgrid(xlim,ylim)

ax.plot_surface(x, y, z(x,y), color = 'k', linewidth = 0, antialiased = False)
#c = ['lime' if a_d_ratio[i] == 1 else ('w') for i in range(len(a_d_ratio))]
#c = [2 if a_d_ratio[i] == 1 else (0) for i in range(len(a_d_ratio))]
ax.scatter3D(X[Y==0,0], X[Y==0,1], X[Y==0,2], c = 'r', s = s)
ax.scatter3D(X[Y==1,0], X[Y==1,1], X[Y==1,2], c = 'b', s = 0.5)

#cmap = plt.get_cmap('bwr', 2)
#sc = ax.scatter(X[:, 0], X[:, 1], X[:, 2], c=Y, cmap=cmap, s = 0.1)
ax.set_xlabel(r'$\lambda$' + ": Lambda", fontsize = 20, labelpad = 20)
ax.set_xlim(0, max(lam))
ax.set_ylabel(r'$\alpha$' + ": Alpha", fontsize = 20, labelpad = 20)
ax.set_ylim(0, max(alpha))
ax.set_zlabel(r'$\delta$' + ": Delta", fontsize = 20, labelpad = 20)
ax.set_zlim(0, max(delta))
ax.tick_params(axis='both', which='major', pad = 5)

red_patch = mpatches.Patch(color='red', label='V(i) / K(i) > 1')
blue_patch = mpatches.Patch(color='blue', label='V(i) / K(i) <= 1')

plt.legend(handles=[red_patch, blue_patch], fontsize = 20, loc='upper right')
plt.title("Cube Search for " + str(len(X[:,0])) + " Patients for PSI = 0.9", fontsize = 30, fontweight = 2)

plt.show()
#z = lambda x,y: (-svc.intercept_[0]-svc.coef_[0][0]*x-svc.coef_[0][1]*y) / svc.coef_[0][2]
print(str(svc.coef_[0][0]) + "," + str(svc.coef_[0][1]) + "," + str(svc.coef_[0][2]) + "," + str(svc.intercept_[0]))
print("a = " + str(svc.coef_[0][0]) + "; b = " + str(svc.coef_[0][1]) + "; c = " + str(svc.coef_[0][2]) + "; d = " + str(svc.intercept_[0]) + ";")

print(str(svc.coef_[0][0]) + "x + ", end = '')
print(str(svc.coef_[0][1]) + "y + " , end = '')
print(str(svc.coef_[0][2]) + "z + ", end = '') 
print(str(svc.intercept_[0]) + " = 0")
print(svc.n_iter_)
print("Linear: " + str(svc.score(X,Y)))

svc = svm.SVC()
svc.fit(X,Y)
print("RBF: " + str(svc.score(X,Y)))
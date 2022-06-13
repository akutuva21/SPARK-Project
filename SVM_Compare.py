import numpy as np
from sklearn import svm
import pandas as pd

param = pd.read_csv("Grid Search psi_0.9 100000_pts.csv")

lam, alpha, delta, psi_check = param.iloc[:, 0], param.iloc[:, 1], param.iloc[:, 2], param.iloc[:, 3]
lam, alpha, delta, psi_check = map(pd.Series, zip(*sorted(zip(delta, lam, alpha, psi_check), reverse=True)))

X = np.hstack((lam.values[:, None], alpha.values[:, None], delta.values[:, None]))
Y = psi_check.to_numpy()

svc = svm.LinearSVC(class_weight=('balanced'))
svc.fit(X,Y)
print(svc.score(X,Y))

svc = svm.LinearSVC()
svc.fit(X,Y)
print(svc.score(X,Y))

for i in range(840, 870, 5):
    normsvc = svm.SVC(C = i)
    normsvc.fit(X,Y)
    print(str(i) + ": " + str(normsvc.score(X,Y)))
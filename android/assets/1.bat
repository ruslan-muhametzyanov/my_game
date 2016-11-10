:treeProcess
for %%f in (*.png) do start optipng %%f
for /D %%d in (*) do (
    cd %%d
    call :treeProcess
    cd ..
)
exit /b
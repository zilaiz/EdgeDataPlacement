# Solution for Native Library (opl1210.dylib) not loaded under MacOS 
1. First make sure to write the following binary file directory to PATH (**Same for Windows**)
   ```shell
   CPLEX_Studio_Community1210/opl/bin/x86-64_osx，CPLEX_Studio_Community1210/opl/oplide,CPLEX_Studio_Community1210/cplex/bin/x86-64_osx,CPLEX_Studio_Community1210/cpopimizer/bin/x86-64_osx
   ```
2. According to [IBM](https://community.ibm.com/community/user/datascience/communities/community-home/digestviewer/viewthread?GroupId=5557&MessageKey=7cee64b6-1c80-4b6a-8a0f-112b7a6ccbb1&CommunityKey=ab7de0fd-6f43-47a9-8261-33578a231bb7&tab=digestviewer)，the reason is that @rpath's path search is wrong, the following is the solution ：
3. First execute the `install_name_tool command` to replace the absolute path to rpath
    ```shell
    install_name_tool -change @rpath/libcplex12100.jnilib /Applications/CPLEX_Studio_Community1210/cplex/bin/x86-64_osx/libcplex12100.jnilib /Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx/libcp_wrap_cpp_java12100.jnilib
    ```
4. Then add */Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx* in java.library.path ，by adding the following args in `Confuguration -> VM options`(`launch.json -> vmArgs` for VSCode)
   ```shell
   -Djava.library.path=/Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx
   ```
   
    

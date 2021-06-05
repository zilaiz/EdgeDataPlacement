# MacOS下Native Library (opl1210.dylib) 未加载的解决方案
1. 首先确保将以下二进制文件目录写入PATH(**此条Windows同理**)
   ```shell
   CPLEX_Studio_Community1210/opl/bin/x86-64_osx，CPLEX_Studio_Community1210/opl/oplide,CPLEX_Studio_Community1210/cplex/bin/x86-64_osx,CPLEX_Studio_Community1210/cpopimizer/bin/x86-64_osx
   ```
2. 根据[IBM](https://community.ibm.com/community/user/datascience/communities/community-home/digestviewer/viewthread?GroupId=5557&MessageKey=7cee64b6-1c80-4b6a-8a0f-112b7a6ccbb1&CommunityKey=ab7de0fd-6f43-47a9-8261-33578a231bb7&tab=digestviewer)，原因在于@rpath的路径寻找错误，以下为解决方案：
3. 首先执行install_name_tool命令对rpath替换绝对路径
    ```shell
    install_name_tool -change @rpath/libcplex12100.jnilib /Applications/CPLEX_Studio_Community1210/cplex/bin/x86-64_osx/libcplex12100.jnilib /Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx/libcp_wrap_cpp_java12100.jnilib
    ```
4. 接着在java.library.path中添加 */Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx* ，具体方式是在程序执行的Confuguration的VM options(VSCode中是launch.json下的"vmArgs"参数)中，添加
   ```shell
   -Djava.library.path=/Applications/CPLEX_Studio_Community1210/cpoptimizer/bin/x86-64_osx
   ```
   
    

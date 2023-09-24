// 获取公钥
keytool -list -rfc -keystore jwt.jks -storepass 810905
// 获取私钥
keytool -v -importkeystore -srckeystore jwt.jks -srcstoretype jks -srcstorepass 810905 -destkeystore jwt.pfx -deststoretype pkcs12 -deststorepass 810905 -destkeypass 810905
// 导出私钥
openssl pkcs12 -in jwt.pfx -nocerts -nodes -out server.key
// 提取公钥（证书）
keytool -list -rfc -keystore jwt.jks -storepass 810905

//生成证书文件：
keytool -genkey -alias server_cert -keypass 12345678 -keyalg RSA -keysize 1024 -validity 365
-keystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.keystore -storepass 87654321

// 查看证书详情：
keytool -list -v -keystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.keystore 
-storepass 87654321

// keystore文件生成cer文件：
keytool -export -alias server_cert 
-keystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.keystore
-file C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\tomcat_server.cer

// 打印证书信息：
keytool -list -rfc -keystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.keystore 
-storepass 87654321

// 提取公钥（证书）:
// 将上一步（打印证书信息）中公钥信息复制到server.txt中保存后，将server.txt文件重新命名为：server.cer文件即可。

// 提取私钥
keytool -v -importkeystore 
-srckeystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.keystore 
-srcstoretype jks -srcstorepass 87654321 
-destkeystore C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.pfx 
-deststoretype pkcs12 -deststorepass 876543210 -destkeypass 12345678
// 其中原密钥口令为：12345678
	
openssl pkcs12 -in C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.pfx -nocerts -nodes 
-out C:\Users\xxxx\Desktop\jarsigner\self-signed-certificate\server.key
// 其中Enter Import Password：876543210

# 简化模式：http://localhost:12011/oauth/authorize?response_type=token&client_id=client-app&redirect_uri=http://www.baidu.com&scope=all
# 授权码模式：http://localhost:12011/oauth/authorize?client_id=client-app&redirect_uri=http://www.baidu.com&response_type=code&scope=all
# nuwa-cloud
# 第一次提交

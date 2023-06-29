
文档地址：https://cloud.tencent.com/developer/article/2099600?from=15425

使用D:\AndroidDevelop\sdk\build-tools\30.0.3\lib\apksigner.jar进行签名


> apksigner sign --verbose --ks <keystore> --ks-key-alias <别名> --ks-pass <pass:123456> --key-pass <pass:123456> --in <待签名的apk> --out <签名后输出的apk>

> apksigner sign --verbose  --ks test.jks --ks-key-alias test --ks-pass pass:123456 --key-pass pass:123456 --in app-debug.apk  --out appsigned.apk

> apksigner sign --verbose  --ks bition --ks-key-alias bition --ks-pass pass:123456 --key-pass pass:123456 --in app-debug.apk  --out appsignn.apk



参数：
--verbose 签名/验证时输出详细信息
--ks 密钥库位置
--ks-key-alias 别名
--ks-pass KeyStore密码
--key-pass 签署者的密码，即生成jks时指定alias对应的密码
--in 被签名的apk
--out 签名过的apk



//直接用apksigner进行验证
apksigner verify -v --print-certs (apk地址)

//使用Jar命令进行验证
java -jar apksigner.jar   verify -v --print-certs (apk地址)
// 加密数据函数 工具crypto.js 文件工具
/**
 * @word 要加密的内容
 * @keyWord String  服务器随机返回的关键字
 *  */
function aesEncrypt(word,keyWord){
  var key = CryptoJS.enc.Utf8.parse(keyWord);
  var srcs = CryptoJS.enc.Utf8.parse(word);
  var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
  return encrypted.toString();
}

/**
 * @word 要解密的内容
 * @keyWord String  服务器随机返回的关键字
 *  */
function aesDecrypt(word,keyWord){
  var key = CryptoJS.enc.Utf8.parse(keyWord);
  var decrypted = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
  return decrypted.toString(CryptoJS.enc.Utf8);
}
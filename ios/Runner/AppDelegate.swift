import UIKit
import Flutter
import Firebase // Add this import
import IOSSecuritySuite
@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate, MessagingDelegate  {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    FirebaseApp.configure()
    Messaging.messaging().delegate = self

// 1
        let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
        
        // 2
        let deviceChannel = FlutterMethodChannel(name: "test.flutter.methodchannel/iOS",
                                                 binaryMessenger: controller.binaryMessenger)
        
        // 3
        prepareMethodHandler(deviceChannel: deviceChannel)


      GeneratedPluginRegistrant.register(with: self)
    if #available(iOS 10.0, *) {
        // For iOS 10 display notification (sent via APNS)
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
    } else {
        let settings: UIUserNotificationSettings =
        UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
        application.registerUserNotificationSettings(settings)
    }
    application.registerForRemoteNotifications()
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
  private func prepareMethodHandler(deviceChannel: FlutterMethodChannel) {
        
        // 4
        deviceChannel.setMethodCallHandler({
            (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            
            // 5
            if call.method == "getDeviceModel" {
                
                // 6
                self.receiveDeviceModel(result: result)
            }
            else {
                // 9
                result(FlutterMethodNotImplemented)
                return
            }
            
        })
    }
    
    private func receiveDeviceModel(result: FlutterResult) {
        // 7
      if IOSSecuritySuite.amIJailbroken() {
            result(true)
        } else if IOSSecuritySuite.amIReverseEngineered(){
            result(true)
        }
        else if IOSSecuritySuite.amIDebugged(){
            result(false)
        }
        else if IOSSecuritySuite.amIRunInEmulator(){
            result(false)
        }
        else{
          result(false)
        }
        
    }

}
/* @UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
} */

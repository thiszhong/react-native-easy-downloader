/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Button,
  Alert,
  PermissionsAndroid,
  Platform
} from 'react-native';
import RNDM from 'react-native-easy-downloader';

const testUrl = 'http://app.mi.com/download/548359?id=cn.mamaguai.cms.xiangli';

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
    super(props);
    this.state = {
      hasPermission: Platform.OS === 'android' && Platform.Version < 23
    }
    this._requestStoragePermission();
  }

  // Android 6.0 获取用于下载文件的存储权限（权限清单AndroidManifest.xml和这里动态获取都需要）
  _requestStoragePermission = async () => {
    if (Platform.OS !== 'android') return;
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
        {
          title: '申请存储权限',
          message: '我要给你下载一个好东西',
          buttonNeutral: 'Next times',
          buttonNegative: 'NO',
          buttonPositive: 'OK',
        },
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.setState({ hasPermission: true })
      }
    } catch (err) {
      console.warn(err);
    }
  }

  render() {
    const { hasPermission } = this.state;

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Download files and install apk for android
        </Text>
        <Button
          onPress={() => {
            RNDM.download({
              url: testUrl,
            }).then(
              ret => Alert.alert('Download success ', ret)
            ).catch (
              err => Alert.alert('Something wrong', err.message)
            )
          }}
          title="Download only"
          color="#841584"
        />
        <View style={styles.separator}></View>
        <Button
          onPress={() => {
            RNDM.installApk('test').then(
              // success
            ).catch (
              err => Alert.alert('Something wrong', err.message)
            )
          }}
          title="Install only"
          color="#841584"
        />
        <View style={styles.separator}></View>
        <Button
          onPress={() => {
            if (!hasPermission) {
              this._requestStoragePermission();
              return
            }
            RNDM.download({
              url: testUrl,
              autoInstall: true,
              savePath: RNDM.DirDownload + '/test.apk',
              title: 'test',
              description: 'v2.1.4',
            }).then(
              ret => console.log('Success ', ret)
            ).catch (
              err => Alert.alert('Something wrong', err.message)
            )
          }}
          title="Download and install"
          color="#841584"
        />
        <View style={styles.separator}></View>
        <Text style={styles.instructions}>Static props:</Text>
        <Text>DirDownload: {RNDM.DirDownload}</Text>
        <View style={styles.separator}></View>
        <Text>DirExternalStorage: {RNDM.DirExternalStorage}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  separator: {
    height: 15,
    width: 100,
  }
});

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
  NativeModules,
  Button,
  Alert
} from 'react-native';
import DM from 'react-native-easy-download-manager';

// const DM = NativeModules.MyDownload;

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Download and install apk for android
        </Text>
        <Button
          onPress={() => {
            DM.download({
              url: 'http://imtt.dd.qq.com/16891/786C74AFC59C29458CD2F3D6922F49DC.apk?fsname=cn.mamaguai.cms.xiangli_2.0.4_204.apk&csr=1bbd',
            }).then(
              ret => Alert.alert('The file path is', ret)
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
            DM.installApk('test').then(
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
            DM.download({
              url: 'http://imtt.dd.qq.com/16891/786C74AFC59C29458CD2F3D6922F49DC.apk?fsname=cn.mamaguai.cms.xiangli_2.0.4_204.apk&csr=1bbd',
              autoInstall: true
            }).then(
              ret => Alert.alert('The file path is', ret)
            ).catch (
              err => Alert.alert('Something wrong', err.message)
            )
          }}
          title="Download and install"
          color="#841584"
        />
        <View style={styles.separator}></View>
        <Text style={styles.instructions}>Static props:</Text>
        <Text>DM.DirDownload: {DM.DirDownload}</Text>
        <Text>DM.DirExternalStorage: {DM.DirExternalStorage}</Text>
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
    borderColor: '#ccc',
    borderWidth: 1,
  },
  separator: {
    height: 15,
    width: 100,
  }
});

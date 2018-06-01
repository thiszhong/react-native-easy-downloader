/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  NativeModules 
} from 'react-native';

const DM = NativeModules.MyDownload;

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Download and install apk for android
        </Text>
        <Text 
          onPress={() => DM.download('http://imtt.dd.qq.com/16891/786C74AFC59C29458CD2F3D6922F49DC.apk?fsname=cn.mamaguai.cms.xiangli_2.0.4_204.apk&csr=1bbd', 'title', 'desc')}
          style={styles.instructions}>
          下载
        </Text>
        <Text style={styles.instructions}>
          安装
        </Text>
        <Text style={styles.instructions}>
          下载并安装
        </Text>
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
});

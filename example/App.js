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
import RNDM from 'react-native-easy-downloader';

const testUrl = 'http://img.rulili.com/xuanpin/software/1806/qhz_v2.0.5_20180606_update.apk';

type Props = {};
export default class App extends Component<Props> {
  render() {
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
            RNDM.download({
              url: testUrl,
              autoInstall: true,
              savePath: RNDM.DirDownload + '/test.apk',
              title: 'test',
              description: 'v2.0.5',
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

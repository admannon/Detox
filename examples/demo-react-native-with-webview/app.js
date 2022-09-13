/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableOpacity
} from 'react-native';
import WebView from "react-native-webview";

class example extends Component {
  constructor(props) {
    super(props);
  }
  render() {
    return (
      <WebView testId="webView"
        source={{ uri: 'https://mui.com/material-ui/react-text-field/' }}
        style={{ marginTop: 0 }}
      />
    );
  }
}

AppRegistry.registerComponent('example', () => example);

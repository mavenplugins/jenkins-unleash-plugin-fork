/*
 * The MIT License
 *
 * Copyright (c) 2009, NDS Group Ltd., James Nord, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.itemis.jenkins.plugins.unleash;

/**
 * @author Markus Hoffrogge
 */
public class UnleashScmCredentialArguments {

  /**
   * true if SSH credentials are to be used
   */
  private boolean useSshCredentials;

  /**
   * If not blank, then this is an environment var name to be used for the user name or the SSH private key
   */
  private String userNameOrSshPrivateKeyEnvVar;

  /**
   * If not blank, then this is an environment var name to be used for the password or SSH passphrase
   */
  private String passphraseEnvVar;

  /**
   * The user name or SSH private key
   */
  private String userNameOrSshPrivateKey;

  /**
   * The password or SSH private key passphrase
   */
  private String passphrase;

  public boolean isUseSshCredentials() {
    return this.useSshCredentials;
  }

  public void setUseSshCredentials(boolean useSshCredentials) {
    this.useSshCredentials = useSshCredentials;
  }

  public String getUserNameOrSshPrivateKeyEnvVar() {
    return this.userNameOrSshPrivateKeyEnvVar;
  }

  public void setUserNameOrSshPrivateKeyEnvVar(String userNameOrSshPrivateKeyEnvVar) {
    this.userNameOrSshPrivateKeyEnvVar = userNameOrSshPrivateKeyEnvVar;
  }

  public String getPassphraseEnvVar() {
    return this.passphraseEnvVar;
  }

  public void setPassphraseEnvVar(String passphraseEnvVar) {
    this.passphraseEnvVar = passphraseEnvVar;
  }

  public String getUserNameOrSshPrivateKey() {
    return this.userNameOrSshPrivateKey;
  }

  public void setUserNameOrSshPrivateKey(String userNameOrSshPrivateKey) {
    this.userNameOrSshPrivateKey = userNameOrSshPrivateKey;
  }

  public String getPassphrase() {
    return this.passphrase;
  }

  public void setPassphrase(String passphrase) {
    this.passphrase = passphrase;
  }

}

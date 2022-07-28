/*
 * Copyright (c) 2017, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.jx.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.jx.JSONXLang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

@ExportLibrary(InteropLibrary.class)
@SuppressWarnings("static-method")
public final class JXBigNumber implements TruffleObject, Comparable<JXBigNumber> {

  private static final Logger logger = LoggerFactory.getLogger(JXBigNumber.class);
  private static final long LONG_MAX_SAFE_DOUBLE = 9007199254740991L; // 2 ** 53 - 1
  private static final int INT_MAX_SAFE_FLOAT = 16777215; // 2 ** 24 - 1

  private static boolean inSafeDoubleRange(long l) {
    return l >= -LONG_MAX_SAFE_DOUBLE && l <= LONG_MAX_SAFE_DOUBLE;
  }

  private static boolean inSafeFloatRange(int i) {
    return i >= -INT_MAX_SAFE_FLOAT && i <= INT_MAX_SAFE_FLOAT;
  }

  private final BigDecimal value;

  public JXBigNumber(BigDecimal value) {
    this.value = value;
  }

  public JXBigNumber(long value) {
    this.value = BigDecimal.valueOf(value);
  }

  public BigDecimal getValue() {
    return value;
  }

  @TruffleBoundary
  public int compareTo(JXBigNumber o) {
    return value.compareTo(o.getValue());
  }

  @Override
  @TruffleBoundary
  public String toString() {
    return value.toString();
  }

  @Override
  @TruffleBoundary
  public boolean equals(Object obj) {
    if (obj instanceof JXBigNumber) {
      return value.equals(((JXBigNumber) obj).getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @SuppressWarnings("static-method")
  @ExportMessage
  boolean isNumber() {
    return true;
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInByte() {
    try{
      value.byteValueExact();
      return true;
    }catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInShort() {
    try {
      value.shortValueExact();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInFloat() {
    try {
      value.floatValue();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInLong() {
    try{
      value.longValueExact();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInInt() {
    try{
      value.intValueExact();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  boolean fitsInDouble() {
    try{
      value.doubleValue();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @ExportMessage
  @TruffleBoundary
  double asDouble() throws UnsupportedMessageException {
    if (fitsInDouble()) {
      return value.doubleValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  @TruffleBoundary
  long asLong() throws UnsupportedMessageException {
    if (fitsInLong()) {
      return value.longValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  @TruffleBoundary
  byte asByte() throws UnsupportedMessageException {
    if (fitsInByte()) {
      return value.byteValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  @TruffleBoundary
  int asInt() throws UnsupportedMessageException {
    if (fitsInInt()) {
      return value.intValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  @TruffleBoundary
  float asFloat() throws UnsupportedMessageException {
    if (fitsInFloat()) {
      return value.floatValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  @TruffleBoundary
  short asShort() throws UnsupportedMessageException {
    if (fitsInShort()) {
      return value.shortValue();
    } else {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return JSONXLang.class;
  }

  @ExportMessage
  boolean hasMetaObject() {
    return true;
  }

  @ExportMessage
  Object getMetaObject() {
    return JXType.NUMBER;
  }

  @ExportMessage
  @TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return value.toString();
  }
}

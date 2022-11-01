/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class MyAccount {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected MyAccount(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MyAccount obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_MyAccount(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setAccIprm(SWIGTYPE_p_OnIncomingCallParam value) {
    pjsua2JNI.MyAccount_accIprm_set(swigCPtr, this, SWIGTYPE_p_OnIncomingCallParam.getCPtr(value));
  }

  public SWIGTYPE_p_OnIncomingCallParam getAccIprm() {
    long cPtr = pjsua2JNI.MyAccount_accIprm_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_OnIncomingCallParam(cPtr, false);
  }

  public void setCall(SWIGTYPE_p_Call value) {
    pjsua2JNI.MyAccount_call_set(swigCPtr, this, SWIGTYPE_p_Call.getCPtr(value));
  }

  public SWIGTYPE_p_Call getCall() {
    long cPtr = pjsua2JNI.MyAccount_call_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_Call(cPtr, false);
  }

  public MyAccount() {
    this(pjsua2JNI.new_MyAccount(), true);
  }

  public void onIncomingCall(SWIGTYPE_p_OnIncomingCallParam iprm) {
    pjsua2JNI.MyAccount_onIncomingCall(swigCPtr, this, SWIGTYPE_p_OnIncomingCallParam.getCPtr(iprm));
  }

}
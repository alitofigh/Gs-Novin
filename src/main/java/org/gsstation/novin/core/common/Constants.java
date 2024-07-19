package org.gsstation.novin.core.common;

/**
 * Created by A_Tofigh at 07/17/2024
 */
public interface Constants {
    public static final String SHETAB_BIN = "950001";
    public static final String SHETAB_BIN_DML_Switch = "603769";
    public static final String BANCS_BIN = "627760";
    public static final String MIZAN_BIN = "950002";


    public static final String NETWORK_NMI_SIGN_ON = "001";
    public static final String NETWORK_NMI_SIGN_OFF = "002";
    public static final String NETWORK_NMI_CHANGE_MAC_KEY = "164";
    public static final String NETWORK_NMI_CHANGE_PIN_KEY = "165";
    public static final String NETWORK_NMI_CUT_OVER = "201";
    public static final String NETWORK_NMI_ECHO_TEST = "301";
    public static final int NETWORK_CHANGE_ISS_PIN = 0;
    public static final int NETWORK_CHANGE_ACQ_PIN = 1;
    public static final int NETWORK_CHANGE_ISS_MAC = 2;
    public static final int NETWORK_CHANGE_ACQ_MAC = 3;

    //Space Keys
    public static final String BANCS_MUX = "mux.bancsMux";
    public static final String SWITCH_MUX = "mux.switchMux";
    public static final String FARAZ_MUX = "mux.farazMux";
    public static final String FARAZ_B_MUX = "mux.farazBTx";
    public static final String SIMIA_MUX = "mux.simiaMux";
    public static final String SIMIA_TX = "SimiaTx";
    public static final String TERMINAL_MUX = "mux.terminalMux";
    public static final String REVERSAL_MUX = "mux.reversalMux";
    public static final String SEND_MIZAN = "MIZANSEND.TXN";
    public static final String SEND_TERMINALS = "TERMINALS.TXN";
    public static final String SEND_SWITCH = "SWITCHSEND.TXN";
    public static final String SEND_RESPONSE = "RESPONSE.TXN";
    public static final String SEND_REVERSAL_FORWARDER = "REFORWARDERRESP.TXN";
    public static final String TRXMGR_KEY = "MSG_Key";
    public static final String TRXMGR_RESP_KEY = "MSG_RESP_Key";
    public static final String SWITCH_CA = "SwitchTx.ready";
    public static final String REVERSAL_MUX_KEY = "ReversalInKey";
    public static final String TERMINAL_KEY_HOLDER = "TerminalKeyHolder";
    public static final String TERMINAL_STATUS_HOLDER = "TerminalStatusHolder";
    public static final String BANK_KEY_HOLDER = "KeyHolder";
    public static final String CONFIGURATION_LOADER = "ConfigurationLoader";
    public static final String AUDIT_NO_SEQUENCER = "auditNoSequencer";
    public static final String REFRENCE_NO_SEQUENCER = "ReferenceNoSequencer";
    public static final String TERMINAL_COMMANDS_HOLDER = "TerminalCommandsHolder";
    public static final String TSPACE = "tspace:default";
    public static final String SAVE_MSG = "SAVE_MSG";
    //MTI
    public static final String MTI_AUTHORIZE = "0100";
    public static final String MTI_AUTHORIZE_RESPONSE = "0110";
    public static final String MTI_FINANCIAL = "0200";
    public static final String MTI_FINANCIAL_RESPONSE = "0210";
    public static final String MTI_REVERSAL = "0400";
    public static final String MTI_REVERSAL_RESPONSE = "0410";
    public static final String MTI_REVERSAL_ADVICE = "0420";
    public static final String MTI_REVERSAL_ADVICE_RESPONSE = "0430";
    public static final String MTI_RECONCILE_ACQ = "0500";
    public static final String MTI_RECONCILE_ADVICE_ACQ = "0520";
    public static final String MTI_RECONCILE_RESPONSE_ACQ = "0510";
    public static final String MTI_RECONCILE_ADVICE_RESPONSE_ACQ = "0530";
    public static final String MTI_RECONCILE_ISS = "0502";
    public static final String MTI_RECONCILE_ADVICE_ISS = "0522";
    public static final String MTI_RECONCILE_RESPONSE_ISS = "0512";
    public static final String MTI_RECONCILE_ADVICE_RESPONSE_ISS = "0532";
    public static final String MTI_NETWORK = "0800";
    public static final String MTI_NETWORK_RESPONSE = "0810";
    public static final String MTI_NETWORK_ADVICE = "0820";
    public static final String MTI_NETWORK_ADVICE_RESPONSE = "0830";

    //Reconcile Status
    public static final String NOT_RECONCILED = "0";
    public static final String RECONCILED = "1";

    public static final String MONITORING_OBSERVER = "monitoringObserver";
    public static final String TERMINAL_MONITORING_SPACE_KEY = "TMonitoring";
    public static final String TERMINAL_MONITORING_OBSERVER = "terminalMonitoringObserver";
    public static final String RESPONSE_TIME_MONITORING_SPACE_KEY = "RespMonitoring";
    public static final String RESPONSE_TIME_MONITORING_OBSERVER = "RespMonitoringObserver";
    public static final String REVERSAL_UPDATER = "ReversalUpdater";

    public static final String PREVIOUS_RESULT = "PREVIOUS_RESULT";
    public static final String PREVIOUS_RESULT_CODE = "PREVIOUS_RESULT_CODE";
    public static final String INVALID_GROUP = "InvalidGroup";
    public static final String BALANCE = "BALANCE";
    public static final String CARD_ACCOUNT_NUMBER = "CARD_ACCOUNT_NUMBER";
    public static final String CARD_TYPE = "CARD_TYPE";
    public static final String ACQUIRER = "AQUIRER";
    public static final String REVERSALMUXES = "REVERSALMUXES";
    public static final String ISSIMIAREQUEST = "ISSIMIAREQUEST";
    public static final String INVALID_SIMIA_TRANSACTION = "INVALID_SIMIA_TRANSACTION";
}

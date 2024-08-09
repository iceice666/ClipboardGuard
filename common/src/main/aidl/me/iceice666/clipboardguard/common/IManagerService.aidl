// IManagerService.aidl
package me.iceice666.clipboardguard.common;

// Declare any non-default types here with import statements
import me.iceice666.clipboardguard.common.datakind.RuleSets;
import me.iceice666.clipboardguard.common.datakind.MessagePacket;


interface IManagerService {
    List<MessagePacket> getLogs();
    void clearLogs();
    int getLogCount();

    void syncRuleSets(in RuleSets ruleSets);
}
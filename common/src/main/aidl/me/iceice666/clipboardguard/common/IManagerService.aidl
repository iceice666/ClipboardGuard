// IManagerService.aidl
package me.iceice666.clipboardguard.common;

// Declare any non-default types here with import statements
import me.iceice666.clipboardguard.common.FieldSelector;
import me.iceice666.clipboardguard.common.RuleSets;
import me.iceice666.clipboardguard.common.MessagePacket;


interface IManagerService {
    void writeLog(in MessagePacket message);
    RuleSets requestRuleSets(in FieldSelector field);
}
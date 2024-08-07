// IManagerService.aidl
package me.iceice666.clipboardguard.common;

// Declare any non-default types here with import statements
import me.iceice666.clipboardguard.common.FieldSelector;
import me.iceice666.clipboardguard.common.RegexSet;
import me.iceice666.clipboardguard.common.MessagePacket;


interface IManagerService {
    void writeLog(in MessagePacket message);
    RegexSet requestRuleSets(in FieldSelector field);
}
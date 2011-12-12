/*
 * This source file is part of OSTIS (Open Semantic Technology for Intelligent
 * Systems) For the latest info, see http://www.ostis.net
 *
 * Copyright (c) 2010 OSTIS
 *
 * OSTIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OSTIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OSTIS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.ostis.scpdev.editors.scp;

/**
 * @author Dmitry Lazurkin
 */
public interface IScpWords {
    public static String[] SCP_OPERATORS = new String[] {
            "add", "add_to_queue", "call", "callReturn", "contAssign", "contErase", "div", "eraseEl", "eraseElStr3",
            "eraseElStr5", "eraseSetStr3", "genEl", "genElStr3", "genElStr5", "gsub", "idtfAssign", "idtfErase",
            "idtfMove", "ifCoin", "ifEq", "ifFormCont", "ifFormIdtf", "ifGr", "ifGrEq", "ifNumber", "ifString",
            "ifType", "ifVarAssign", "init", "mult", "nop", "pow", "print", "printEl", "printNl", "procedure",
            "program", "programSCP", "return", "searchElStr3", "searchElStr5", "searchSetStr3", "searchSetStr5",
            "selectNStr3", "selectNStr5", "selectYStr3", "selectYStr5", "sub", "sys_close_segment",
            "sys_create_segment", "sys_get_autosegment", "sys_get_default_segment", "sys_get_location", "sys_open_dir",
            "sys_open_dir_uri", "sys_open_segment", "sys_open_segment_uri", "sys_send_message",
            "sys_set_default_segment", "sys_set_event_handler", "sys_spin_segment", "sys_unlink", "sys_wait",
            "varAssign", "varErase", "waitReturn", "wait_erase_element", "wait_gen_input_arc", "wait_gen_output_arc",
            "wait_input_arc", "wait_output_arc", "wait_recieve_message"
    };

    public static String[] SCP_ORDINALS = new String[] {
            "1_", "2_", "3_", "4_", "5_", "6_", "7_", "8_", "9_", "set1_", "set2_", "set3_", "set4_", "set5_", "set6_",
            "set7_", "set8_", "set9_"
    };

    public static String[] SCP_ATTRIBUTES = new String[] {
            "arc_", "assign_", "const_", "else_", "f_", "fixed_", "fuz_", "goto_", "in_", "init_", "metavar_", "neg_",
            "node_", "out_", "pos_", "prm_", "segc_1_", "segc_2_", "segc_3_", "segc_4_", "segc_5_", "segc_6_",
            "segc_7_", "segc_8_", "segc_9_", "then_", "undf_", "var_"
    };

}

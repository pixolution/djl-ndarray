/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.ndarray.types;

import ai.djl.ndarray.NDArray;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/** An enum representing the underlying {@link NDArray}'s data type. */
public enum DataType {
    // do not change order, pytorch engine rely on DataType.ordinal()
    FLOAT32(Format.FLOATING, 4),
    FLOAT64(Format.FLOATING, 8),
    FLOAT16(Format.FLOATING, 2),
    UINT8(Format.UINT, 1),
    INT32(Format.INT, 4),
    INT8(Format.INT, 1),
    INT64(Format.INT, 8),
    BOOLEAN(Format.BOOLEAN, 1),
    COMPLEX64(Format.FLOATING, 4),
    UNKNOWN(Format.UNKNOWN, 0),
    STRING(Format.STRING, -1),
    BFLOAT16(Format.FLOATING, 2),
    UINT64(Format.UINT, 8),
    UINT32(Format.UINT, 4),
    UINT16(Format.UINT, 2),
    INT16(Format.INT, 2);

    /** The general data type format categories. */
    public enum Format {
        FLOATING,
        UINT,
        INT,
        BOOLEAN,
        STRING,
        UNKNOWN
    }

    private Format format;
    private int numOfBytes;

    DataType(Format format, int numOfBytes) {
        this.format = format;
        this.numOfBytes = numOfBytes;
    }

    /**
     * Returns the number of bytes for each element.
     *
     * @return the number of bytes for each element
     */
    public int getNumOfBytes() {
        return numOfBytes;
    }

    /**
     * Returns the format of the data type.
     *
     * @return the format of the data type
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Checks whether it is a floating data type.
     *
     * @return whether it is a floating data type
     */
    public boolean isFloating() {
        return format == Format.FLOATING;
    }

    /**
     * Checks whether it is an integer data type.
     *
     * @return whether it is an integer type
     */
    public boolean isInteger() {
        return format == Format.UINT || format == Format.INT;
    }

    /**
     * Checks whether it is a boolean data type.
     *
     * @return whether it is a boolean data type
     */
    public boolean isBoolean() {
        return format == Format.BOOLEAN;
    }

    /**
     * Returns the data type to use for a data buffer.
     *
     * @param data the buffer to analyze
     * @return the data type for the buffer
     */
    public static DataType fromBuffer(Buffer data) {
        if (data instanceof FloatBuffer) {
            return DataType.FLOAT32;
        } else if (data instanceof ShortBuffer) {
            return DataType.FLOAT16;
        } else if (data instanceof DoubleBuffer) {
            return DataType.FLOAT64;
        } else if (data instanceof IntBuffer) {
            return DataType.INT32;
        } else if (data instanceof LongBuffer) {
            return DataType.INT64;
        } else if (data instanceof ByteBuffer) {
            return DataType.INT8;
        } else {
            throw new IllegalArgumentException(
                    "Unsupported buffer type: " + data.getClass().getSimpleName());
        }
    }

    /**
     * Returns the data type from numpy value.
     *
     * @param dtype the numpy datatype
     * @return the data type
     */
    public static DataType fromNumpy(String dtype) {
        switch (dtype) {
            case "<f4":
            case ">f4":
            case "=f4":
                return FLOAT32;
            case "<f8":
            case ">f8":
            case "=f8":
                return FLOAT64;
            case "<f2":
            case ">f2":
            case "=f2":
                return FLOAT16;
            case "|u1":
                return UINT8;
            case "<u2":
            case ">u2":
            case "=u2":
                return UINT16;
            case "<u4":
            case ">u4":
            case "=u4":
                return UINT32;
            case "<u8":
            case ">u8":
            case "=u8":
                return UINT64;
            case "|i1":
                return INT8;
            case "<i2":
            case ">i2":
            case "=i2":
                return INT16;
            case "<i4":
            case ">i4":
            case "=i4":
                return INT32;
            case "<i8":
            case ">i8":
            case "=i8":
                return INT64;
            case "|b1":
                return BOOLEAN;
            case "|S1":
                return STRING;
            default:
                throw new IllegalArgumentException("Unsupported dataType: " + dtype);
        }
    }

    /**
     * Returns the data type from Safetensors value.
     *
     * @param dtype the Safetensors datatype
     * @return the data type
     */
    public static DataType fromSafetensors(String dtype) {
        switch (dtype) {
            case "F64":
                return FLOAT64;
            case "F32":
                return FLOAT32;
            case "F16":
                return FLOAT16;
            case "BF16":
                return BFLOAT16;
            case "I64":
                return INT64;
            case "I32":
                return INT32;
            case "I8":
                return INT8;
            case "U8":
                return UINT8;
            case "BOOL":
                return BOOLEAN;
            default:
                throw new IllegalArgumentException("Unsupported safetensors dataType: " + dtype);
        }
    }

    /**
     * Converts a {@link ByteBuffer} to a buffer for this data type.
     *
     * @param data the buffer to convert
     * @return the converted buffer
     */
    public Buffer asDataType(ByteBuffer data) {
        switch (this) {
            case FLOAT16:
            case BFLOAT16:
                return data.asShortBuffer();
            case FLOAT32:
                return data.asFloatBuffer();
            case FLOAT64:
                return data.asDoubleBuffer();
            case INT32:
            case UINT32:
                return data.asIntBuffer();
            case INT64:
            case UINT64:
                return data.asLongBuffer();
            case UINT8:
            case INT8:
            case COMPLEX64:
            case UNKNOWN:
            default:
                return data;
        }
    }

    /**
     * Returns a numpy string value.
     *
     * @return a numpy string value
     */
    public String asNumpy() {
        char order = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? '>' : '<';
        switch (this) {
            case FLOAT32:
                return order + "f4";
            case FLOAT64:
                return order + "f8";
            case FLOAT16:
                return order + "f2";
            case UINT8:
                return "|u1";
            case UINT16:
                return order + "u2";
            case UINT32:
                return order + "u4";
            case UINT64:
                return order + "u8";
            case INT8:
                return "|i1";
            case INT16:
                return order + "i2";
            case INT32:
                return order + "i4";
            case INT64:
                return order + "i8";
            case BOOLEAN:
                return "|b1";
            case STRING:
                return "|S1";
            case BFLOAT16:
            case COMPLEX64:
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("Unsupported dataType: " + this);
        }
    }

    /**
     * Returns a safetensors string value.
     *
     * @return a safetensors string value
     */
    public String asSafetensors() {
        switch (this) {
            case FLOAT64:
                return "F64";
            case FLOAT32:
                return "F32";
            case FLOAT16:
                return "F16";
            case BFLOAT16:
                return "BF16";
            case INT64:
                return "I64";
            case INT32:
                return "I32";
            case INT8:
                return "I8";
            case UINT8:
                return "U8";
            case BOOLEAN:
                return "BOOL";
            case INT16:
            case UINT64:
            case UINT32:
            case UINT16:
            case STRING:
            case COMPLEX64:
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("Unsupported dataType: " + this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

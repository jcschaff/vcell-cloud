#
# Autogenerated by Thrift Compiler (0.10.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
#  options string: py
#

from thrift.Thrift import TType, TMessageType, TFrozenDict, TException, TApplicationException
from thrift.protocol.TProtocol import TProtocolException
import sys
import logging
from .ttypes import *
from thrift.Thrift import TProcessor
from thrift.transport import TTransport


class Iface(object):
    def getDataset(self, simInfo):
        """
        Parameters:
         - simInfo
        """
        pass

    def getStatus(self, simInfo):
        """
        Parameters:
         - simInfo
        """
        pass

    def computeModel(self, model, simSpec):
        """
        Parameters:
         - model
         - simSpec
        """
        pass


class Client(Iface):
    def __init__(self, iprot, oprot=None):
        self._iprot = self._oprot = iprot
        if oprot is not None:
            self._oprot = oprot
        self._seqid = 0

    def getDataset(self, simInfo):
        """
        Parameters:
         - simInfo
        """
        self.send_getDataset(simInfo)
        return self.recv_getDataset()

    def send_getDataset(self, simInfo):
        self._oprot.writeMessageBegin('getDataset', TMessageType.CALL, self._seqid)
        args = getDataset_args()
        args.simInfo = simInfo
        args.write(self._oprot)
        self._oprot.writeMessageEnd()
        self._oprot.trans.flush()

    def recv_getDataset(self):
        iprot = self._iprot
        (fname, mtype, rseqid) = iprot.readMessageBegin()
        if mtype == TMessageType.EXCEPTION:
            x = TApplicationException()
            x.read(iprot)
            iprot.readMessageEnd()
            raise x
        result = getDataset_result()
        result.read(iprot)
        iprot.readMessageEnd()
        if result.success is not None:
            return result.success
        if result.dataAccessException is not None:
            raise result.dataAccessException
        raise TApplicationException(TApplicationException.MISSING_RESULT, "getDataset failed: unknown result")

    def getStatus(self, simInfo):
        """
        Parameters:
         - simInfo
        """
        self.send_getStatus(simInfo)
        return self.recv_getStatus()

    def send_getStatus(self, simInfo):
        self._oprot.writeMessageBegin('getStatus', TMessageType.CALL, self._seqid)
        args = getStatus_args()
        args.simInfo = simInfo
        args.write(self._oprot)
        self._oprot.writeMessageEnd()
        self._oprot.trans.flush()

    def recv_getStatus(self):
        iprot = self._iprot
        (fname, mtype, rseqid) = iprot.readMessageBegin()
        if mtype == TMessageType.EXCEPTION:
            x = TApplicationException()
            x.read(iprot)
            iprot.readMessageEnd()
            raise x
        result = getStatus_result()
        result.read(iprot)
        iprot.readMessageEnd()
        if result.success is not None:
            return result.success
        if result.dataAccessException is not None:
            raise result.dataAccessException
        raise TApplicationException(TApplicationException.MISSING_RESULT, "getStatus failed: unknown result")

    def computeModel(self, model, simSpec):
        """
        Parameters:
         - model
         - simSpec
        """
        self.send_computeModel(model, simSpec)
        return self.recv_computeModel()

    def send_computeModel(self, model, simSpec):
        self._oprot.writeMessageBegin('computeModel', TMessageType.CALL, self._seqid)
        args = computeModel_args()
        args.model = model
        args.simSpec = simSpec
        args.write(self._oprot)
        self._oprot.writeMessageEnd()
        self._oprot.trans.flush()

    def recv_computeModel(self):
        iprot = self._iprot
        (fname, mtype, rseqid) = iprot.readMessageBegin()
        if mtype == TMessageType.EXCEPTION:
            x = TApplicationException()
            x.read(iprot)
            iprot.readMessageEnd()
            raise x
        result = computeModel_result()
        result.read(iprot)
        iprot.readMessageEnd()
        if result.success is not None:
            return result.success
        if result.dataAccessException is not None:
            raise result.dataAccessException
        raise TApplicationException(TApplicationException.MISSING_RESULT, "computeModel failed: unknown result")


class Processor(Iface, TProcessor):
    def __init__(self, handler):
        self._handler = handler
        self._processMap = {}
        self._processMap["getDataset"] = Processor.process_getDataset
        self._processMap["getStatus"] = Processor.process_getStatus
        self._processMap["computeModel"] = Processor.process_computeModel

    def process(self, iprot, oprot):
        (name, type, seqid) = iprot.readMessageBegin()
        if name not in self._processMap:
            iprot.skip(TType.STRUCT)
            iprot.readMessageEnd()
            x = TApplicationException(TApplicationException.UNKNOWN_METHOD, 'Unknown function %s' % (name))
            oprot.writeMessageBegin(name, TMessageType.EXCEPTION, seqid)
            x.write(oprot)
            oprot.writeMessageEnd()
            oprot.trans.flush()
            return
        else:
            self._processMap[name](self, seqid, iprot, oprot)
        return True

    def process_getDataset(self, seqid, iprot, oprot):
        args = getDataset_args()
        args.read(iprot)
        iprot.readMessageEnd()
        result = getDataset_result()
        try:
            result.success = self._handler.getDataset(args.simInfo)
            msg_type = TMessageType.REPLY
        except (TTransport.TTransportException, KeyboardInterrupt, SystemExit):
            raise
        except ThriftDataAccessException as dataAccessException:
            msg_type = TMessageType.REPLY
            result.dataAccessException = dataAccessException
        except Exception as ex:
            msg_type = TMessageType.EXCEPTION
            logging.exception(ex)
            result = TApplicationException(TApplicationException.INTERNAL_ERROR, 'Internal error')
        oprot.writeMessageBegin("getDataset", msg_type, seqid)
        result.write(oprot)
        oprot.writeMessageEnd()
        oprot.trans.flush()

    def process_getStatus(self, seqid, iprot, oprot):
        args = getStatus_args()
        args.read(iprot)
        iprot.readMessageEnd()
        result = getStatus_result()
        try:
            result.success = self._handler.getStatus(args.simInfo)
            msg_type = TMessageType.REPLY
        except (TTransport.TTransportException, KeyboardInterrupt, SystemExit):
            raise
        except ThriftDataAccessException as dataAccessException:
            msg_type = TMessageType.REPLY
            result.dataAccessException = dataAccessException
        except Exception as ex:
            msg_type = TMessageType.EXCEPTION
            logging.exception(ex)
            result = TApplicationException(TApplicationException.INTERNAL_ERROR, 'Internal error')
        oprot.writeMessageBegin("getStatus", msg_type, seqid)
        result.write(oprot)
        oprot.writeMessageEnd()
        oprot.trans.flush()

    def process_computeModel(self, seqid, iprot, oprot):
        args = computeModel_args()
        args.read(iprot)
        iprot.readMessageEnd()
        result = computeModel_result()
        try:
            result.success = self._handler.computeModel(args.model, args.simSpec)
            msg_type = TMessageType.REPLY
        except (TTransport.TTransportException, KeyboardInterrupt, SystemExit):
            raise
        except ThriftDataAccessException as dataAccessException:
            msg_type = TMessageType.REPLY
            result.dataAccessException = dataAccessException
        except Exception as ex:
            msg_type = TMessageType.EXCEPTION
            logging.exception(ex)
            result = TApplicationException(TApplicationException.INTERNAL_ERROR, 'Internal error')
        oprot.writeMessageBegin("computeModel", msg_type, seqid)
        result.write(oprot)
        oprot.writeMessageEnd()
        oprot.trans.flush()

# HELPER FUNCTIONS AND STRUCTURES


class getDataset_args(object):
    """
    Attributes:
     - simInfo
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRUCT, 'simInfo', (SimulationInfo, SimulationInfo.thrift_spec), None, ),  # 1
    )

    def __init__(self, simInfo=None,):
        self.simInfo = simInfo

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.STRUCT:
                    self.simInfo = SimulationInfo()
                    self.simInfo.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('getDataset_args')
        if self.simInfo is not None:
            oprot.writeFieldBegin('simInfo', TType.STRUCT, 1)
            self.simInfo.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class getDataset_result(object):
    """
    Attributes:
     - success
     - dataAccessException
    """

    thrift_spec = (
        (0, TType.STRUCT, 'success', (Dataset, Dataset.thrift_spec), None, ),  # 0
        (1, TType.STRUCT, 'dataAccessException', (ThriftDataAccessException, ThriftDataAccessException.thrift_spec), None, ),  # 1
    )

    def __init__(self, success=None, dataAccessException=None,):
        self.success = success
        self.dataAccessException = dataAccessException

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 0:
                if ftype == TType.STRUCT:
                    self.success = Dataset()
                    self.success.read(iprot)
                else:
                    iprot.skip(ftype)
            elif fid == 1:
                if ftype == TType.STRUCT:
                    self.dataAccessException = ThriftDataAccessException()
                    self.dataAccessException.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('getDataset_result')
        if self.success is not None:
            oprot.writeFieldBegin('success', TType.STRUCT, 0)
            self.success.write(oprot)
            oprot.writeFieldEnd()
        if self.dataAccessException is not None:
            oprot.writeFieldBegin('dataAccessException', TType.STRUCT, 1)
            self.dataAccessException.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class getStatus_args(object):
    """
    Attributes:
     - simInfo
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRUCT, 'simInfo', (SimulationInfo, SimulationInfo.thrift_spec), None, ),  # 1
    )

    def __init__(self, simInfo=None,):
        self.simInfo = simInfo

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.STRUCT:
                    self.simInfo = SimulationInfo()
                    self.simInfo.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('getStatus_args')
        if self.simInfo is not None:
            oprot.writeFieldBegin('simInfo', TType.STRUCT, 1)
            self.simInfo.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class getStatus_result(object):
    """
    Attributes:
     - success
     - dataAccessException
    """

    thrift_spec = (
        (0, TType.STRUCT, 'success', (SimulationStatus, SimulationStatus.thrift_spec), None, ),  # 0
        (1, TType.STRUCT, 'dataAccessException', (ThriftDataAccessException, ThriftDataAccessException.thrift_spec), None, ),  # 1
    )

    def __init__(self, success=None, dataAccessException=None,):
        self.success = success
        self.dataAccessException = dataAccessException

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 0:
                if ftype == TType.STRUCT:
                    self.success = SimulationStatus()
                    self.success.read(iprot)
                else:
                    iprot.skip(ftype)
            elif fid == 1:
                if ftype == TType.STRUCT:
                    self.dataAccessException = ThriftDataAccessException()
                    self.dataAccessException.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('getStatus_result')
        if self.success is not None:
            oprot.writeFieldBegin('success', TType.STRUCT, 0)
            self.success.write(oprot)
            oprot.writeFieldEnd()
        if self.dataAccessException is not None:
            oprot.writeFieldBegin('dataAccessException', TType.STRUCT, 1)
            self.dataAccessException.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class computeModel_args(object):
    """
    Attributes:
     - model
     - simSpec
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRUCT, 'model', (SBMLModel, SBMLModel.thrift_spec), None, ),  # 1
        (2, TType.STRUCT, 'simSpec', (SimulationSpec, SimulationSpec.thrift_spec), None, ),  # 2
    )

    def __init__(self, model=None, simSpec=None,):
        self.model = model
        self.simSpec = simSpec

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.STRUCT:
                    self.model = SBMLModel()
                    self.model.read(iprot)
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.STRUCT:
                    self.simSpec = SimulationSpec()
                    self.simSpec.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('computeModel_args')
        if self.model is not None:
            oprot.writeFieldBegin('model', TType.STRUCT, 1)
            self.model.write(oprot)
            oprot.writeFieldEnd()
        if self.simSpec is not None:
            oprot.writeFieldBegin('simSpec', TType.STRUCT, 2)
            self.simSpec.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class computeModel_result(object):
    """
    Attributes:
     - success
     - dataAccessException
    """

    thrift_spec = (
        (0, TType.STRUCT, 'success', (SimulationInfo, SimulationInfo.thrift_spec), None, ),  # 0
        (1, TType.STRUCT, 'dataAccessException', (ThriftDataAccessException, ThriftDataAccessException.thrift_spec), None, ),  # 1
    )

    def __init__(self, success=None, dataAccessException=None,):
        self.success = success
        self.dataAccessException = dataAccessException

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 0:
                if ftype == TType.STRUCT:
                    self.success = SimulationInfo()
                    self.success.read(iprot)
                else:
                    iprot.skip(ftype)
            elif fid == 1:
                if ftype == TType.STRUCT:
                    self.dataAccessException = ThriftDataAccessException()
                    self.dataAccessException.read(iprot)
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('computeModel_result')
        if self.success is not None:
            oprot.writeFieldBegin('success', TType.STRUCT, 0)
            self.success.write(oprot)
            oprot.writeFieldEnd()
        if self.dataAccessException is not None:
            oprot.writeFieldBegin('dataAccessException', TType.STRUCT, 1)
            self.dataAccessException.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)

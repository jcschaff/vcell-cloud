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

from thrift.transport import TTransport


class ReferenceVariableType(object):
    independent = 0
    dependent = 1

    _VALUES_TO_NAMES = {
        0: "independent",
        1: "dependent",
    }

    _NAMES_TO_VALUES = {
        "independent": 0,
        "dependent": 1,
    }


class OptimizationParameterType(object):
    Number_of_Generations = 0
    Number_of_Iterations = 1
    Population_Size = 2
    Random_Number_Generator = 3
    Seed = 4
    IterationLimit = 5
    Tolerance = 6
    Rho = 7
    Scale = 8
    Swarm_Size = 9
    Std_Deviation = 10
    Start_Temperature = 11
    Cooling_Factor = 12
    Pf = 13

    _VALUES_TO_NAMES = {
        0: "Number_of_Generations",
        1: "Number_of_Iterations",
        2: "Population_Size",
        3: "Random_Number_Generator",
        4: "Seed",
        5: "IterationLimit",
        6: "Tolerance",
        7: "Rho",
        8: "Scale",
        9: "Swarm_Size",
        10: "Std_Deviation",
        11: "Start_Temperature",
        12: "Cooling_Factor",
        13: "Pf",
    }

    _NAMES_TO_VALUES = {
        "Number_of_Generations": 0,
        "Number_of_Iterations": 1,
        "Population_Size": 2,
        "Random_Number_Generator": 3,
        "Seed": 4,
        "IterationLimit": 5,
        "Tolerance": 6,
        "Rho": 7,
        "Scale": 8,
        "Swarm_Size": 9,
        "Std_Deviation": 10,
        "Start_Temperature": 11,
        "Cooling_Factor": 12,
        "Pf": 13,
    }


class OptimizationParameterDataType(object):
    INT = 0
    DOUBLE = 1

    _VALUES_TO_NAMES = {
        0: "INT",
        1: "DOUBLE",
    }

    _NAMES_TO_VALUES = {
        "INT": 0,
        "DOUBLE": 1,
    }


class OptimizationMethodType(object):
    EvolutionaryProgram = 0
    SRES = 1
    GeneticAlgorithm = 2
    GeneticAlgorithmSR = 3
    HookeJeeves = 4
    LevenbergMarquardt = 5
    NelderMead = 6
    ParticleSwarm = 7
    RandomSearch = 8
    SimulatedAnnealing = 9
    SteepestDescent = 10
    Praxis = 11
    TruncatedNewton = 12

    _VALUES_TO_NAMES = {
        0: "EvolutionaryProgram",
        1: "SRES",
        2: "GeneticAlgorithm",
        3: "GeneticAlgorithmSR",
        4: "HookeJeeves",
        5: "LevenbergMarquardt",
        6: "NelderMead",
        7: "ParticleSwarm",
        8: "RandomSearch",
        9: "SimulatedAnnealing",
        10: "SteepestDescent",
        11: "Praxis",
        12: "TruncatedNewton",
    }

    _NAMES_TO_VALUES = {
        "EvolutionaryProgram": 0,
        "SRES": 1,
        "GeneticAlgorithm": 2,
        "GeneticAlgorithmSR": 3,
        "HookeJeeves": 4,
        "LevenbergMarquardt": 5,
        "NelderMead": 6,
        "ParticleSwarm": 7,
        "RandomSearch": 8,
        "SimulatedAnnealing": 9,
        "SteepestDescent": 10,
        "Praxis": 11,
        "TruncatedNewton": 12,
    }


class ParameterDescription(object):
    """
    Attributes:
     - name
     - scale
     - minValue
     - maxValue
     - initialValue
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRING, 'name', 'UTF8', None, ),  # 1
        (2, TType.DOUBLE, 'scale', None, None, ),  # 2
        (3, TType.DOUBLE, 'minValue', None, None, ),  # 3
        (4, TType.DOUBLE, 'maxValue', None, None, ),  # 4
        (5, TType.DOUBLE, 'initialValue', None, None, ),  # 5
    )

    def __init__(self, name=None, scale=None, minValue=None, maxValue=None, initialValue=None,):
        self.name = name
        self.scale = scale
        self.minValue = minValue
        self.maxValue = maxValue
        self.initialValue = initialValue

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
                if ftype == TType.STRING:
                    self.name = iprot.readString().decode('utf-8') if sys.version_info[0] == 2 else iprot.readString()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.DOUBLE:
                    self.scale = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 3:
                if ftype == TType.DOUBLE:
                    self.minValue = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 4:
                if ftype == TType.DOUBLE:
                    self.maxValue = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 5:
                if ftype == TType.DOUBLE:
                    self.initialValue = iprot.readDouble()
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
        oprot.writeStructBegin('ParameterDescription')
        if self.name is not None:
            oprot.writeFieldBegin('name', TType.STRING, 1)
            oprot.writeString(self.name.encode('utf-8') if sys.version_info[0] == 2 else self.name)
            oprot.writeFieldEnd()
        if self.scale is not None:
            oprot.writeFieldBegin('scale', TType.DOUBLE, 2)
            oprot.writeDouble(self.scale)
            oprot.writeFieldEnd()
        if self.minValue is not None:
            oprot.writeFieldBegin('minValue', TType.DOUBLE, 3)
            oprot.writeDouble(self.minValue)
            oprot.writeFieldEnd()
        if self.maxValue is not None:
            oprot.writeFieldBegin('maxValue', TType.DOUBLE, 4)
            oprot.writeDouble(self.maxValue)
            oprot.writeFieldEnd()
        if self.initialValue is not None:
            oprot.writeFieldBegin('initialValue', TType.DOUBLE, 5)
            oprot.writeDouble(self.initialValue)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.name is None:
            raise TProtocolException(message='Required field name is unset!')
        if self.scale is None:
            raise TProtocolException(message='Required field scale is unset!')
        if self.minValue is None:
            raise TProtocolException(message='Required field minValue is unset!')
        if self.maxValue is None:
            raise TProtocolException(message='Required field maxValue is unset!')
        if self.initialValue is None:
            raise TProtocolException(message='Required field initialValue is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class ReferenceVariable(object):
    """
    Attributes:
     - varName
     - varType
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRING, 'varName', 'UTF8', None, ),  # 1
        (2, TType.I32, 'varType', None, None, ),  # 2
    )

    def __init__(self, varName=None, varType=None,):
        self.varName = varName
        self.varType = varType

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
                if ftype == TType.STRING:
                    self.varName = iprot.readString().decode('utf-8') if sys.version_info[0] == 2 else iprot.readString()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.I32:
                    self.varType = iprot.readI32()
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
        oprot.writeStructBegin('ReferenceVariable')
        if self.varName is not None:
            oprot.writeFieldBegin('varName', TType.STRING, 1)
            oprot.writeString(self.varName.encode('utf-8') if sys.version_info[0] == 2 else self.varName)
            oprot.writeFieldEnd()
        if self.varType is not None:
            oprot.writeFieldBegin('varType', TType.I32, 2)
            oprot.writeI32(self.varType)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.varName is None:
            raise TProtocolException(message='Required field varName is unset!')
        if self.varType is None:
            raise TProtocolException(message='Required field varType is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class CopasiOptimizationParameter(object):
    """
    Attributes:
     - paramType
     - value
     - dataType
    """

    thrift_spec = (
        None,  # 0
        (1, TType.I32, 'paramType', None, None, ),  # 1
        (2, TType.DOUBLE, 'value', None, None, ),  # 2
        (3, TType.I32, 'dataType', None, None, ),  # 3
    )

    def __init__(self, paramType=None, value=None, dataType=None,):
        self.paramType = paramType
        self.value = value
        self.dataType = dataType

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
                if ftype == TType.I32:
                    self.paramType = iprot.readI32()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.DOUBLE:
                    self.value = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 3:
                if ftype == TType.I32:
                    self.dataType = iprot.readI32()
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
        oprot.writeStructBegin('CopasiOptimizationParameter')
        if self.paramType is not None:
            oprot.writeFieldBegin('paramType', TType.I32, 1)
            oprot.writeI32(self.paramType)
            oprot.writeFieldEnd()
        if self.value is not None:
            oprot.writeFieldBegin('value', TType.DOUBLE, 2)
            oprot.writeDouble(self.value)
            oprot.writeFieldEnd()
        if self.dataType is not None:
            oprot.writeFieldBegin('dataType', TType.I32, 3)
            oprot.writeI32(self.dataType)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.paramType is None:
            raise TProtocolException(message='Required field paramType is unset!')
        if self.value is None:
            raise TProtocolException(message='Required field value is unset!')
        if self.dataType is None:
            raise TProtocolException(message='Required field dataType is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class CopasiOptimizationMethod(object):
    """
    Attributes:
     - optimizationMethodType
     - optimizationParameterList
    """

    thrift_spec = (
        None,  # 0
        (1, TType.I32, 'optimizationMethodType', None, None, ),  # 1
        (2, TType.LIST, 'optimizationParameterList', (TType.STRUCT, (CopasiOptimizationParameter, CopasiOptimizationParameter.thrift_spec), False), None, ),  # 2
    )

    def __init__(self, optimizationMethodType=None, optimizationParameterList=None,):
        self.optimizationMethodType = optimizationMethodType
        self.optimizationParameterList = optimizationParameterList

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
                if ftype == TType.I32:
                    self.optimizationMethodType = iprot.readI32()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.LIST:
                    self.optimizationParameterList = []
                    (_etype3, _size0) = iprot.readListBegin()
                    for _i4 in range(_size0):
                        _elem5 = CopasiOptimizationParameter()
                        _elem5.read(iprot)
                        self.optimizationParameterList.append(_elem5)
                    iprot.readListEnd()
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
        oprot.writeStructBegin('CopasiOptimizationMethod')
        if self.optimizationMethodType is not None:
            oprot.writeFieldBegin('optimizationMethodType', TType.I32, 1)
            oprot.writeI32(self.optimizationMethodType)
            oprot.writeFieldEnd()
        if self.optimizationParameterList is not None:
            oprot.writeFieldBegin('optimizationParameterList', TType.LIST, 2)
            oprot.writeListBegin(TType.STRUCT, len(self.optimizationParameterList))
            for iter6 in self.optimizationParameterList:
                iter6.write(oprot)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.optimizationMethodType is None:
            raise TProtocolException(message='Required field optimizationMethodType is unset!')
        if self.optimizationParameterList is None:
            raise TProtocolException(message='Required field optimizationParameterList is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class DataRow(object):
    """
    Attributes:
     - data
    """

    thrift_spec = (
        None,  # 0
        (1, TType.LIST, 'data', (TType.DOUBLE, None, False), None, ),  # 1
    )

    def __init__(self, data=None,):
        self.data = data

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
                if ftype == TType.LIST:
                    self.data = []
                    (_etype10, _size7) = iprot.readListBegin()
                    for _i11 in range(_size7):
                        _elem12 = iprot.readDouble()
                        self.data.append(_elem12)
                    iprot.readListEnd()
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
        oprot.writeStructBegin('DataRow')
        if self.data is not None:
            oprot.writeFieldBegin('data', TType.LIST, 1)
            oprot.writeListBegin(TType.DOUBLE, len(self.data))
            for iter13 in self.data:
                oprot.writeDouble(iter13)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.data is None:
            raise TProtocolException(message='Required field data is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class DataSet(object):
    """
    Attributes:
     - rows
    """

    thrift_spec = (
        None,  # 0
        (1, TType.LIST, 'rows', (TType.STRUCT, (DataRow, DataRow.thrift_spec), False), None, ),  # 1
    )

    def __init__(self, rows=None,):
        self.rows = rows

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
                if ftype == TType.LIST:
                    self.rows = []
                    (_etype17, _size14) = iprot.readListBegin()
                    for _i18 in range(_size14):
                        _elem19 = DataRow()
                        _elem19.read(iprot)
                        self.rows.append(_elem19)
                    iprot.readListEnd()
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
        oprot.writeStructBegin('DataSet')
        if self.rows is not None:
            oprot.writeFieldBegin('rows', TType.LIST, 1)
            oprot.writeListBegin(TType.STRUCT, len(self.rows))
            for iter20 in self.rows:
                iter20.write(oprot)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.rows is None:
            raise TProtocolException(message='Required field rows is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class OptProblem(object):
    """
    Attributes:
     - mathModelSbmlContents
     - numberOfOptimizationRuns
     - parameterDescriptionList
     - referenceVariableList
     - experimentalDataSet
     - optimizationMethod
    """

    thrift_spec = (
        None,  # 0
        (1, TType.STRING, 'mathModelSbmlContents', 'UTF8', None, ),  # 1
        (2, TType.I32, 'numberOfOptimizationRuns', None, None, ),  # 2
        (3, TType.LIST, 'parameterDescriptionList', (TType.STRUCT, (ParameterDescription, ParameterDescription.thrift_spec), False), None, ),  # 3
        (4, TType.LIST, 'referenceVariableList', (TType.STRUCT, (ReferenceVariable, ReferenceVariable.thrift_spec), False), None, ),  # 4
        (5, TType.STRUCT, 'experimentalDataSet', (DataSet, DataSet.thrift_spec), None, ),  # 5
        (6, TType.STRUCT, 'optimizationMethod', (CopasiOptimizationMethod, CopasiOptimizationMethod.thrift_spec), None, ),  # 6
    )

    def __init__(self, mathModelSbmlContents=None, numberOfOptimizationRuns=None, parameterDescriptionList=None, referenceVariableList=None, experimentalDataSet=None, optimizationMethod=None,):
        self.mathModelSbmlContents = mathModelSbmlContents
        self.numberOfOptimizationRuns = numberOfOptimizationRuns
        self.parameterDescriptionList = parameterDescriptionList
        self.referenceVariableList = referenceVariableList
        self.experimentalDataSet = experimentalDataSet
        self.optimizationMethod = optimizationMethod

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
                if ftype == TType.STRING:
                    self.mathModelSbmlContents = iprot.readString().decode('utf-8') if sys.version_info[0] == 2 else iprot.readString()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.I32:
                    self.numberOfOptimizationRuns = iprot.readI32()
                else:
                    iprot.skip(ftype)
            elif fid == 3:
                if ftype == TType.LIST:
                    self.parameterDescriptionList = []
                    (_etype24, _size21) = iprot.readListBegin()
                    for _i25 in range(_size21):
                        _elem26 = ParameterDescription()
                        _elem26.read(iprot)
                        self.parameterDescriptionList.append(_elem26)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            elif fid == 4:
                if ftype == TType.LIST:
                    self.referenceVariableList = []
                    (_etype30, _size27) = iprot.readListBegin()
                    for _i31 in range(_size27):
                        _elem32 = ReferenceVariable()
                        _elem32.read(iprot)
                        self.referenceVariableList.append(_elem32)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            elif fid == 5:
                if ftype == TType.STRUCT:
                    self.experimentalDataSet = DataSet()
                    self.experimentalDataSet.read(iprot)
                else:
                    iprot.skip(ftype)
            elif fid == 6:
                if ftype == TType.STRUCT:
                    self.optimizationMethod = CopasiOptimizationMethod()
                    self.optimizationMethod.read(iprot)
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
        oprot.writeStructBegin('OptProblem')
        if self.mathModelSbmlContents is not None:
            oprot.writeFieldBegin('mathModelSbmlContents', TType.STRING, 1)
            oprot.writeString(self.mathModelSbmlContents.encode('utf-8') if sys.version_info[0] == 2 else self.mathModelSbmlContents)
            oprot.writeFieldEnd()
        if self.numberOfOptimizationRuns is not None:
            oprot.writeFieldBegin('numberOfOptimizationRuns', TType.I32, 2)
            oprot.writeI32(self.numberOfOptimizationRuns)
            oprot.writeFieldEnd()
        if self.parameterDescriptionList is not None:
            oprot.writeFieldBegin('parameterDescriptionList', TType.LIST, 3)
            oprot.writeListBegin(TType.STRUCT, len(self.parameterDescriptionList))
            for iter33 in self.parameterDescriptionList:
                iter33.write(oprot)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        if self.referenceVariableList is not None:
            oprot.writeFieldBegin('referenceVariableList', TType.LIST, 4)
            oprot.writeListBegin(TType.STRUCT, len(self.referenceVariableList))
            for iter34 in self.referenceVariableList:
                iter34.write(oprot)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        if self.experimentalDataSet is not None:
            oprot.writeFieldBegin('experimentalDataSet', TType.STRUCT, 5)
            self.experimentalDataSet.write(oprot)
            oprot.writeFieldEnd()
        if self.optimizationMethod is not None:
            oprot.writeFieldBegin('optimizationMethod', TType.STRUCT, 6)
            self.optimizationMethod.write(oprot)
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        if self.mathModelSbmlContents is None:
            raise TProtocolException(message='Required field mathModelSbmlContents is unset!')
        if self.numberOfOptimizationRuns is None:
            raise TProtocolException(message='Required field numberOfOptimizationRuns is unset!')
        if self.parameterDescriptionList is None:
            raise TProtocolException(message='Required field parameterDescriptionList is unset!')
        if self.referenceVariableList is None:
            raise TProtocolException(message='Required field referenceVariableList is unset!')
        if self.experimentalDataSet is None:
            raise TProtocolException(message='Required field experimentalDataSet is unset!')
        if self.optimizationMethod is None:
            raise TProtocolException(message='Required field optimizationMethod is unset!')
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)

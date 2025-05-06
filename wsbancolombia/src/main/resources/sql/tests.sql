-- VALORES Y CONSULTAS DE PRUEBA
--CREATE TABLE CCV_RECIBO_CONSIGNACION_VIGENT(VALOR NUMBER NOT NULL,FECHA_LIMITE_PAGO DATE NOT NULL,REFERENCIA NUMBER PRIMARY KEY);
--INSERT INTO CCV_RECIBO_CONSIGNACION_VIGENT VALUES(120000, TO_DATE('2016-10-17', 'YYYY-MM-DD'), 111111);
--INSERT INTO CCV_RECIBO_CONSIGNACION_VIGENT VALUES(220000, TO_DATE('2016-10-17', 'YYYY-MM-DD'), 111112);
--INSERT INTO CCV_RECIBO_CONSIGNACION_VIGENT VALUES(320000, TO_DATE('2016-10-17', 'YYYY-MM-DD'), 111113);
--select * from SYS.USER_ERRORS where NAME = 'WS_INSERT_PRB_RECORD' and type = 'PROCEDURE';

DECLARE
STATUS CHAR(1);
DUEDT DATE;
EFFDT DATE;
PAYID NUMBER(6);
BEGIN
    SIA_WS_INSERT_PRB_RECORD ('1234', '5678', 'ALGO', TO_DATE('2016-10-16', 'YYYY-MM-DD'), 120000, 'E', '98765432', '111111', '1234', STATUS, DUEDT, EFFDT, PAYID);
END;
/

SELECT COUNT(ID_FACTURA) FROM SIA_WS_PAGOS_RIN WHERE ID_FACTURA = '111111' AND ESTADO = 'P';

INSERT INTO SIA_WS_PAGOS_RIN(ID_BANCO, ID_SUCURSAL, ID_CAJERO,
FECHA_PAGO, VALOR_PAGO, FORMA_PAGO, NIT_PAGADOR, ID_FACTURA, ID_GRUPO_PAGO, ESTADO) values ('1224','12345','algo',
TO_DATE('10-10-2016', 'mm-dd-yyyy'), 20000, 'E',
'1234567', 'dasdhkas', 'algo', 'P');

DECLARE
  T1 SIA_WS_QUERY_BILLS.SIA_BILL_ARRAY;
  RECIBO SIA_WS_RECIBO_TEMP%ROWTYPE;
BEGIN
  T1 := SIA_WS_QUERY_BILLS.SEARCH_BILL('CLIENTE = 1110548046');
  FOR INDX IN 1 .. T1.COUNT LOOP
    RECIBO := T1(INDX);
    DBMS_OUTPUT.put_line (RECIBO.TIPO_RECIBO);
  END LOOP;
END;

INSERT INTO SIA_WS_RECIBO_TEMP VALUES (1, 1000, SYSDATE, 1, '10', 1, SYSDATE);
INSERT INTO SIA_WS_RECIBO_TEMP VALUES (2, 1000, SYSDATE, 2, '10', 1, SYSDATE);
INSERT INTO SIA_WS_RECIBO_TEMP VALUES (3, 1000, SYSDATE, 3, '10', 1, SYSDATE);
INSERT INTO SIA_WS_RECIBO_TEMP VALUES (4, 1000, SYSDATE, 4, '10', 1, SYSDATE);
INSERT INTO SIA_WS_RECIBO_TEMP VALUES (5, 1000, SYSDATE, 660397, '10', 1, SYSDATE);

DECLARE
  TYPE SIA_BILL_ARRAY IS TABLE OF SIA_WS_RECIBO_TEMP%ROWTYPE;
  BILL_LIST SIA_BILL_ARRAY;
  RECIBO SIA_WS_RECIBO_TEMP%ROWTYPE;
BEGIN
  EXECUTE IMMEDIATE 'SELECT CLIENTE, VALOR, FECHA_LIMITE_PAGO, REFERENCIA,' ||
    'TIPO_RECIBO, NUMERO_CREDITO, SYSDATE FECHA_REGISTRO ' ||
    'FROM CCV_RECIBO_CONSIGNACION_VIGENT WHERE REFERENCIA = 660397'
    BULK COLLECT INTO BILL_LIST;

    FOR INDX IN 1 .. BILL_LIST.COUNT
    LOOP
      RECIBO := BILL_LIST(INDX);
      SIA_WS_INSERT_RECIBO_TEMP(RECIBO);
    END LOOP;
END;

DECLARE
  CURSOR C1 IS SELECT * FROM SIA_WS_RECIBO_TEMP FOR UPDATE;
  RECIBO_TEMP C1%ROWTYPE;
BEGIN
  OPEN C1;
  LOOP
    FETCH C1 INTO RECIBO_TEMP;
    EXIT WHEN C1%NOTFOUND;
    UPDATE SIA_WS_RECIBO_TEMP SET VALOR = 20 WHERE CURRENT OF C1;
  END LOOP;
  CLOSE C1;
END;

---- Error a manejar
--ORA-00001: unique constraint (ICEBERG.UNQ_ID_TRANSACCION) violated
--ORA-06512: at "ICEBERG.SIA_WS_QUERY_BILLS", line 89
--ORA-06512: at line 8
--00001. 00000 -  "unique constraint (%s.%s) violated"
--*Cause:    An UPDATE or INSERT statement attempted to insert a duplicate key.
--           For Trusted Oracle configured in DBMS MAC mode, you may see
--          this message if a duplicate entry exists at a different level.
--*Action:   Either remove the unique restriction or do not insert the key.
------------------------------

SELECT referencia, cliente, TO_CHAR(FECHA_LIMITE_PAGO, 'YYYYMMDD') FECHA_LIMITE,
valor, tipo_recibo, periodo, REFERENCIA_ACADEMICO, DESCRIPCION
FROM CCV_RECIBO_CONSIGNACION_VIGENT
WHERE SUBSTR(REFERENCIA_ACADEMICO,1,3) != 'INS'
AND TRUNC(FECHA_LIMITE_PAGO) = TRUNC(TO_DATE('2016-12-30', 'YYYY-MM-DD'))
AND PERIODO = '2017A' AND ROWNUM < 30;

SELECT * FROM CCV_RECIBO_CONSIGNACION_VIGENT
WHERE TIPO_RECIBO = 'CREDITO'
AND ROWNUM < 11 ORDER BY NUMERO_CREDITO;

SELECT * FROM CCV_RECIBO_CONSIGNACION_VIGENT
WHERE NUMERO_CREDITO=7933;

SELECT CLIENTE, COUNT(CLIENTE) CUENTA
FROM CCV_RECIBO_CONSIGNACION_VIGENT
WHERE TIPO_RECIBO = 'CREDITO'
GROUP BY CLIENTE
HAVING COUNT(CLIENTE) > 1;

SELECT * FROM CCV_RECIBO_CONSIGNACION_VIGENT
WHERE CLIENTE = 1110455948;

SELECT referencia, cliente, TO_CHAR(FECHA_LIMITE_PAGO, 'YYYYMMDD') FECHA_LIMITE,
valor, tipo_recibo, periodo, REFERENCIA_ACADEMICO, DESCRIPCION
FROM CCV_RECIBO_CONSIGNACION_VIGENT
where SUBSTR(REFERENCIA_ACADEMICO,1,3) = 'IDI';

SELECT referencia, cliente, TO_CHAR(FECHA_LIMITE_PAGO, 'YYYYMMDD') FECHA_LIMITE,
valor, tipo_recibo, periodo, REFERENCIA_ACADEMICO, DESCRIPCION
FROM CCV_RECIBO_CONSIGNACION_VIGENT
where cliente in
(1110542834, 1110542826, 1110589139, 96082626139, 1110540114, 1110487220)
order by cliente;

SELECT referencia, cliente, TO_CHAR(FECHA_LIMITE_PAGO, 'YYYYMMDD') FECHA_LIMITE,
valor, tipo_recibo, periodo, REFERENCIA_ACADEMICO, DESCRIPCION
FROM CCV_RECIBO_CONSIGNACION_VIGENT
where referencia = 659269;


SELECT referencia, cliente, TO_CHAR(FECHA_LIMITE_PAGO, 'YYYYMMDD') FECHA_LIMITE,
valor, tipo_recibo, periodo, REFERENCIA_ACADEMICO, DESCRIPCION
FROM CCV_RECIBO_CONSIGNACION_VIGENT
where cliente in
(1110542834, 1110542826, 1110589139, 96082626139, 1110540114, 1110487220)
AND FECHA_LIMITE_PAGO > TO_DATE('2016-12-01', 'YYYY-MM-DD');

declare
type numarray is table of number;
referencias numarray := numarray(1110542834,1110544051,1110542826,1010049536,1110589139,98011555500);
DEBT NUMBER;
begin
for indx in 1 .. referencias.count loop
    SELECT NVL(SUM(VALOR_DOCUMENTO-VALOR_AFECTADO),0) INTO DEBT
    FROM SALDO_CARTERA
    WHERE CLIENTE = referencias(indx)
    AND TRUNC(FECHA_VENCIMIENTO) < TRUNC(SYSDATE);

    IF(DEBT = 0) THEN
      DBMS_OUTPUT.PUT_LINE(TO_CHAR(referencias(indx)));
    END IF;

end loop;
end;


SELECT * FROM SIA_WS_PAGOS_RIN;

DECLARE
  RQUID integer := 1234;
  STATUS CHAR(1);
  DUEDT DATE;
  EFFDT DATE;
  PAYID NUMBER(6);
  REFEREN VARCHAR2(20);
BEGIN
  FOR RECI IN (select REFERENCIA, VALOR from CCV_RECIBO_CONSIGNACION_VIGENT
  where cliente in (1110542834, 1110542826, 1110589139, 96082626139, 1110540114, 1110487220)
  and TIPO_RECIBO='CREDITO' and trunc(FECHA_LIMITE_PAGO) = TRUNC(TO_DATE('2016-11-17', 'YYYY-MM-DD')))
  LOOP
    SIA_WS_QUERY_BILLS.PAY_CURRENT_BILL(TO_CHAR(RQUID), '0101', '001', NULL, SYSDATE, RECI.VALOR,
    'C', 'DANIEL', RECI.REFERENCIA, TO_CHAR(RQUID), STATUS, DUEDT, EFFDT, PAYID, REFEREN);

    DBMS_OUTPUT.put_line(STATUS || '-' || TO_CHAR(PAYID) || '-' || REFEREN);

    RQUID := RQUID + 1;

  END LOOP;
END;

DECLARE
  TYPE TABLITA IS TABLE OF NUMBER;
  REFERENCIAS TABLITA := TABLITA (661754,661881,662013,661637,661794,661931,661597,662053,661821,668643,
  668565,673390,668197,668529,665480,668562,658767,658785,659069,659176,659269,659279,659327,659469,672261,674659,
  674622,673139,674764,673927);
  RF NUMBER;
  TOTAL NUMBER := 0;
BEGIN
    FOR IND IN 1..REFERENCIAS.COUNT LOOP
      SELECT COUNT(CCV.REFERENCIA) INTO RF
      FROM CCV_RECIBO_CONSIGNACION_VIGENT CCV
      LEFT JOIN TET_PAGOS_ONLINE TET ON CCV.REFERENCIA = TET.RECIBO_CONSIGNACION
      WHERE REFERENCIA = REFERENCIAS(IND)  AND TET.RECIBO_CONSIGNACION IS NULL ORDER BY FECHA_LIMITE_PAGO;
      IF RF > 0 THEN
        TOTAL := TOTAL + 1;
      ELSE
        DBMS_OUTPUT.PUT_LINE('REFERENCIA ' || REFERENCIAS(IND) || ' NO ENCONTRADA');
      END IF;
    END LOOP;
    DBMS_OUTPUT.put_line('TOTAL: ' || TOTAL);
END;

------------------------------

BEGIN
  IF (SELECT NVL(SUM(VALOR_DOCUMENTO-VALOR_AFECTADO),0)
  FROM SALDO_CARTERA
  WHERE CLIENTE = RECIBO.CLIENTE
  AND TRUNC(FECHA_VENCIMIENTO) < TRUNC(SYSDATE)) = 0 THEN
    DBMS_OUTPUT.PUT_LINE('LO QUE SEA');
  ELSE
    DBMS_OUTPUT.PUT_LINE('MOROSO');
  END IF;
END;

--1088322204
SELECT NVL(SUM(VALOR_DOCUMENTO-VALOR_AFECTADO),0)
  FROM SALDO_CARTERA
  WHERE CLIENTE = 1110558845
  AND TRUNC(FECHA_VENCIMIENTO) < TRUNC(SYSDATE);

set autotrace on
select * from CCV_RECIBO_CONSIGNACION_VIGENT where cliente = 1006165510 order by FECHA_LIMITE_PAGO;

select * from CCV_RECIBO_CONSIGNACION_VIGENT where substr(REFERENCIA_ACADEMICO, 1, 3) = 'REC';

select * from ccv_recibo_consignacion_vigent  where
                        referencia='661881' and fecha_limite_pago in
                            ( select min(fecha_limite_pago) from ccv_recibo_consignacion_vigent where
                                    referencia='661881' and fecha_limite_pago >= to_date('19-09-2016','dd/mm/yyyy') );


-----------------------------------
DECLARE
  CLIENTE NUMBER(16,0);
  --ORDEN   NUMBER(8,0);
  FECHA_VENCIMIENTO DATE;
  --PORCENTAJE  NUMBER;
  RECIBO_CONSIGNACION NUMBER;
  REFERENCIA_ACADEMICO VARCHAR2(255);
  VALOR_TOTAL NUMBER;
  --VALOR_DETALLE NUMBER;
  --PERIODO VARCHAR2(10);
  --NUMERO_FILA NUMBER;
  --FECHA DATE;
  C1 SYS_REFCURSOR;
  TYPE TABLITA IS TABLE OF NUMBER;
  REFERENCIAS TABLITA := TABLITA (661597,661821,661931,662053,662699,662759,
662762,662766,662769,662871,662873,663648,661575,661589,661617,661679,658767,658785,
659069,659176,659269,659279,659327,659469,672261,673139,673927,674622,674659,674764);
BEGIN

  FOR IND IN 1..REFERENCIAS.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE('----------------------------------------------------');
    C1 := SIA_TEST_QUERY_REF(REFERENCIAS(IND));
    --LOOP
      FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, REFERENCIA_ACADEMICO, VALOR_TOTAL;
      --INTO  CLIENTE, ORDEN, FECHA_VENCIMIENTO, PORCENTAJE, RECIBO_CONSIGNACION, REFERENCIA_ACADEMICO,
      --      VALOR_TOTAL, VALOR_DETALLE, PERIODO, NUMERO_FILA, FECHA;
      --EXIT WHEN C1%NOTFOUND;
      IF C1%FOUND THEN
        DBMS_OUTPUT.PUT_LINE(CLIENTE || ' | ' || RECIBO_CONSIGNACION || ' | ' ||
        REFERENCIA_ACADEMICO || ' | ' || VALOR_TOTAL || ' | ' || FECHA_VENCIMIENTO);
      END IF;
    --END LOOP;
    CLOSE C1;
  END LOOP;
END;

DECLARE
  TYPE TABLITA IS TABLE OF NUMBER;
  REFERENCIAS TABLITA := TABLITA (661597,661575,661589,673927,674622,5555,6666);
  MBILL SIA_BILL_DATA_TYPE;
BEGIN
  FOR IND IN 1..REFERENCIAS.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE('----------------------------------------------------');
    MBILL := SIA_BUSCAR_FACTURAS.TODAS_POR_RC(REFERENCIAS(IND));
    IF MBILL IS NOT NULL THEN
      DBMS_OUTPUT.PUT_LINE(MBILL.CLIENTE || ' | ' || MBILL.REFERENCIA || ' | ' ||
        MBILL.REFERENCIA_ACADEMICO || ' | ' || MBILL.VALOR || ' | ' || MBILL.FECHA_LIMITE_PAGO ||
        ' | ' || MBILL.TIPO_RECIBO);
    END IF;
  END LOOP;
END;

DECLARE
  TYPE TABLITA IS TABLE OF NUMBER;
  REFERENCIAS TABLITA := TABLITA (99040908109,99010705580,1106774642, 777777);
  BILL_LIST SIA_BILL_ARRAY;
  MBILL SIA_BILL_DATA_TYPE;
BEGIN
  FOR IND IN 1..REFERENCIAS.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE('----------------------------------------------------');
    BILL_LIST := SIA_BUSCAR_FACTURAS.TODAS_POR_NIT(REFERENCIAS(IND));
    FOR IND IN 1..BILL_LIST.COUNT LOOP
      MBILL := BILL_LIST(IND);
      DBMS_OUTPUT.PUT_LINE(MBILL.CLIENTE || ' | ' || MBILL.REFERENCIA || ' | ' ||
        MBILL.REFERENCIA_ACADEMICO || ' | ' || MBILL.VALOR || ' | ' || MBILL.FECHA_LIMITE_PAGO ||
        ' | ' || MBILL.TIPO_RECIBO);
    END LOOP;
  END LOOP;
END;

---------------------------------------

CREATE OR REPLACE FUNCTION SIA_SOMETHING_WHATEVER(QTYPE VARCHAR2, QVALUE VARCHAR2)
  RETURN SYS_REFCURSOR AS
  --RETURN SIA_BILL_ARRAY AS
  MBILL SIA_BILL_DATA_TYPE;
  BILL_LIST SIA_BILL_ARRAY := SIA_BILL_ARRAY();
  CURRENT_POS NUMBER := 1;
  NMBR NUMBER := 1;
  RCID NUMBER(8,0);
  RESULT_CURSOR SYS_REFCURSOR;
BEGIN
  IF QTYPE = 'RC' THEN
    LOOP
      SELECT REGEXP_INSTR(QVALUE, ',', CURRENT_POS) INTO NMBR FROM DUAL;
      EXIT WHEN NMBR = 0;
      SELECT TO_NUMBER(SUBSTR(QVALUE, CURRENT_POS, NMBR - CURRENT_POS)) INTO RCID FROM DUAL;
      MBILL := SIA_BUSCAR_FACTURAS.TODAS_POR_RC(RCID);
      IF MBILL IS NOT NULL THEN
        BILL_LIST.EXTEND;
        BILL_LIST(BILL_LIST.LAST) := MBILL;
        INSERT_RECIBO_TEMP(MBILL);
      END IF;
      CURRENT_POS := NMBR + 1;
    END LOOP;

    SELECT TO_NUMBER(SUBSTR(QVALUE, CURRENT_POS)) INTO RCID FROM DUAL;
    MBILL := SIA_BUSCAR_FACTURAS.TODAS_POR_RC(RCID);
    IF MBILL IS NOT NULL THEN
        BILL_LIST.EXTEND;
        BILL_LIST(BILL_LIST.LAST) := MBILL;
        INSERT_RECIBO_TEMP(MBILL);
    END IF;

  ELSIF QTYPE = 'NIT' THEN
    BILL_LIST := SIA_BUSCAR_FACTURAS.TODAS_POR_NIT(QVALUE);
    FOR IND IN 1..BILL_LIST.COUNT LOOP
      MBILL := BILL_LIST(IND);
      INSERT_RECIBO_TEMP(MBILL);
    END LOOP;
  END IF;

  OPEN RESULT_CURSOR FOR SELECT * FROM TABLE(CAST(BILL_LIST AS SIA_BILL_ARRAY));
  RETURN RESULT_CURSOR;
  --RETURN BILL_LIST;
END SIA_SOMETHING_WHATEVER;

DECLARE
  MBILL SIA_BILL_DATA_TYPE;
  BILL_LIST SIA_BILL_ARRAY;
BEGIN
	  BILL_LIST := SIA_SOMETHING_WHATEVER('RC', '661597,661575,661589');
	  FOR IND IN 1..BILL_LIST.COUNT LOOP
		    MBILL := BILL_LIST(IND);
		    DBMS_OUTPUT.PUT_LINE(MBILL.CLIENTE || ' | ' || MBILL.REFERENCIA || ' | ' ||
			        MBILL.REFERENCIA_ACADEMICO || ' | ' || MBILL.VALOR || ' | ' || MBILL.FECHA_LIMITE_PAGO ||
				        ' | ' || MBILL.TIPO_RECIBO);
				  END LOOP;
			END;

DECLARE
  CUENTA NUMBER := 0;
  CLIENTE NUMBER(16,0);
  FECHA_VENCIMIENTO DATE;
  RECIBO_CONSIGNACION NUMBER;
  REFERENCIA_ACADEMICO VARCHAR2(20);
  VALOR_TOTAL NUMBER;
  TIPO_RECIBO VARCHAR2(10);
  C1 SYS_REFCURSOR;
BEGIN
  FOR CLL IN (SELECT CLIENTE FROM CCV_RECIBO_CONSIGNACION_VIGENT GROUP BY CLIENTE) LOOP
    C1 := SIA_BUSCAR_FACTURAS.CUOTA_CREDITO_POR_NIT(CLL.CLIENTE);
    FETCH C1 INTO CLIENTE, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION, VALOR_TOTAL;
    IF C1%FOUND THEN
      DBMS_OUTPUT.PUT_LINE(TO_CHAR(RECIBO_CONSIGNACION));
      CUENTA := CUENTA + 1;
    END IF;
    EXIT WHEN CUENTA = 6;
  END LOOP;
END;

DECLARE
  CUENTA NUMBER := 0;
  BILL_LIST SIA_BILL_ARRAY;
  MBILL SIA_BILL_DATA_TYPE;
BEGIN
  FOR CLL IN (SELECT CLIENTE FROM CCV_RECIBO_CONSIGNACION_VIGENT GROUP BY CLIENTE) LOOP
    BILL_LIST := SIA_BUSCAR_FACTURAS.TODAS_POR_NIT(CLL.CLIENTE);
    IF BILL_LIST.COUNT > 1 THEN
      FOR IND IN 1..BILL_LIST.COUNT LOOP
        MBILL := BILL_LIST(IND);
        DBMS_OUTPUT.PUT_LINE(MBILL.CLIENTE || ' | ' || MBILL.REFERENCIA || ' | ' ||
        MBILL.REFERENCIA_ACADEMICO || ' | ' || MBILL.VALOR || ' | ' || MBILL.FECHA_LIMITE_PAGO ||
        ' | ' || MBILL.TIPO_RECIBO);
      END LOOP;
      CUENTA := CUENTA + 1;
    END IF;
    EXIT WHEN CUENTA = 6;
  END LOOP;
END;

declare
  CLIENTE NUMBER(16,0);
  FECHA_VENCIMIENTO DATE;
  RECIBO_CONSIGNACION NUMBER;
  REFERENCIA_ACADEMICO VARCHAR2(20);
  VALOR_TOTAL NUMBER;
  TIPO_RECIBO VARCHAR2(10);
  C1 SYS_REFCURSOR := SIA_WS_QUERY_BILLS.SEARCH_BILL('NIT', '1110542854');
BEGIN
  LOOP
  FETCH C1 INTO CLIENTE, VALOR_TOTAL, FECHA_VENCIMIENTO, RECIBO_CONSIGNACION,
  REFERENCIA_ACADEMICO, TIPO_RECIBO;
  EXIT WHEN C1%NOTFOUND;
  DBMS_OUTPUT.PUT_LINE('----------------------------------------------------');
  DBMS_OUTPUT.PUT_LINE(CLIENTE || ' | ' || RECIBO_CONSIGNACION || ' | ' ||
        REFERENCIA_ACADEMICO || ' | ' || VALOR_TOTAL || ' | ' || FECHA_VENCIMIENTO || ' | ' || TIPO_RECIBO);
  END LOOP;
END;

-- Consulta para PRE con Grupo = 1
SELECT * FROM VENCIMIENTO_PERIODO WHERE (GRUPO=1) and (PERIODO='2017A') order by GRUPO ,FECHA_VENCIMIENTO;
-- Insertar nuevo periodo para PRE
INSERT INTO VENCIMIENTO_PERIODO(GRUPO,FECHA_VENCIMIENTO,PERIODO,PORCENTAJE,DESCRIPCION_GRUPO,DESCRIPCION_FECHA)
VALUES (1,TO_DATE('2017-03-31', 'YYYY-MM-DD'),'2017A',10,'PRUEBAS BANCOLOMBIA','PRUEBAS BANCOLOMBIA');
-- Consulta para periodo idiomas IDI
SELECT ROWID,PERIODO,NOMBRE_PERIODO,ESTADO,CIERRE_PAGINA,TIPO_PERIODO,INICIO_DIFERIDO,
MESES_DIFERIDO,USA_INF_DIFERIDO_ORDEN_SOLNOTA
FROM PERIODO_FACTURACION WHERE (PERIODO='2016EI') and (TIPO_PERIODO='IDI') order by PERIODO;
-- Consulta para fecha vencimiento
SELECT ROWID,GRUPO,FECHA_VENCIMIENTO,PERIODO,PORCENTAJE,DESCRIPCION_GRUPO,DESCRIPCION_FECHA
FROM VENCIMIENTO_PERIODO WHERE (GRUPO=2) and (PERIODO='2016EI') order by GRUPO ,FECHA_VENCIMIENTO;
-- Insertar
INSERT INTO VENCIMIENTO_PERIODO(GRUPO,FECHA_VENCIMIENTO,PERIODO,PORCENTAJE,DESCRIPCION_GRUPO,DESCRIPCION_FECHA)
VALUES (2,TO_DATE('2017-03-31', 'YYYY-MM-DD'),'2016EI',0,'PRUEBAS BANCOLOMBIA','PRUEBAS BANCOLOMBIA');

select cliente, fecha_vencimiento, recibo_consignacion,
            referencia_academico, valor_detalle_extra,
            valor_sin_extra, porcentaje,
            valor_detalle_extra*(1 + (porcentaje/100)) + valor_sin_extra as valor_mal,
            (valor_detalle_extra + valor_sin_extra)*(1 + (porcentaje/100)) as valor_bien,
            case when porcentaje < 0 then (valor_detalle_extra + valor_sin_extra)*(1 + (porcentaje/100))
            else valor_detalle_extra*(1 + (porcentaje/100)) + valor_sin_extra end as fuckery
        from (select rec.cliente cliente, ven.fecha_vencimiento fecha_vencimiento, ven.porcentaje,
            rec.recibo_consignacion recibo_consignacion, cct.referencia_academico referencia_academico,
            (
                SELECT SUM( DRC.valor ) valor
                FROM detalle_recibo_consignacion DRC, recibo_consignacion RC, tipo_solicitud_nota ts
                WHERE   rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     (NVL(ts.incluye_liquidacion_extra, 'N') = 'S' OR drc.orden > 0)
                AND NOT EXISTS (
                    SELECT 'X' FROM concepto_ingreso_credito
                    WHERE concepto IN ('EX','CX')
                    AND concepto_nota = drc.concepto_nota
                    AND causa_nota = drc.causa_nota)
            ) valor_detalle_extra,
            (
                SELECT nvl(SUM     (DRC.valor),0)   valor
                FROM
                detalle_recibo_consignacion DRC,
                recibo_consignacion RC,
                tipo_solicitud_nota ts
                WHERE  rc.cliente = rec.cliente
                AND     RC.estado IN  ('I', 'C')
                AND     RC.recibo_consignacion = DRC.recibo_consignacion
                AND     ts.concepto_nota (+) = drc.concepto_nota
                AND     ts.causa_nota (+) = drc.causa_nota
                AND     NVL(ts.incluye_liquidacion_extra, 'N') = 'N'
                AND     rc.recibo_consignacion = rec.recibo_consignacion
                AND     drc.orden IS NULL
                AND NOT EXISTS (SELECT 'X'
                FROM concepto_ingreso_credito
                WHERE concepto IN ( 'EX','CX')
                AND concepto_nota = drc.concepto_nota
                AND causa_nota = drc.causa_nota)
            ) valor_sin_extra
            from
            orden ord,
            recibo_consignacion rec,
            vencimiento_periodo ven,
            detalle_recibo_consignacion detcon,
            cct_ordenes_academico cct
            where
            ord.cliente_solicitado = rec.cliente
            and ord.grupo = ven.grupo
            and ord.orden = detcon.orden
            and ord.documento = detcon.documento_orden
            and ord.estado = 'V'
            and rec.estado = 'I'
            and ord.periodo = rec.periodo
            and ord.periodo = ven.periodo
            and ven.periodo = rec.periodo
            and rec.recibo_consignacion = cct.recibo_consignacion
            and rec.cliente = cct.cliente
            and ven.fecha_vencimiento >= TRUNC(SYSDATE)
            and rec.recibo_consignacion = detcon.recibo_consignacion
            and not exists(
                SELECT 'X' FROM tet_pagos_online WHERE recibo_consignacion = rec.recibo_consignacion
            ) and not exists (
                select 	liq.documento,
                liq.orden
                from liquidacion_orden liq
                where liq.estado = 'V'
                and liq.documento_originado = ord.documento
                and liq.orden = ord.orden
                and liq.organizacion_originado = ord.organizacion
            ) and rec.recibo_consignacion= 697340
            )order by cliente, recibo_consignacion, fecha_vencimiento;